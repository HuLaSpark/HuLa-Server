package com.luohuo.basic.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbRuntimeException;
import cn.hutool.db.meta.MetaUtil;
import cn.hutool.db.meta.Table;
import cn.hutool.db.meta.TableType;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.toolkit.JdbcUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据库工具类
 *
 * @author 乾乾
 * @date 2021/11/3 18:32
 */
@Slf4j
public class DbPlusUtil {
    private static final Pattern SQL_SERVER_PATTERN = Pattern.compile("jdbc(:p6spy)?:(?<db>\\w+):.*((//)|@)(?<host>.+):(?<port>\\d+)(/|(;databasename=)|:)(?<dbName>\\w+)\\??.*");
    private static final Map<String, DbType> JDBC_DB_TYPE_CACHE = new ConcurrentHashMap<>();

    @SneakyThrows
    public static String getSqlServerDbName(String url) {
        Matcher m = SQL_SERVER_PATTERN.matcher(url.toLowerCase());
        if (m.find()) {
            return m.group("dbName");
        }
        return null;
    }

    /**
     * 截取jdbc地址中的数据库名
     *
     * @param jdbcUrl jdbc 链接地址
     * @return 数据库类型
     */
    public static String getDataBaseNameByUrl(String jdbcUrl) {
        String database = null;
        int pos, pos1;
        String connUri;

        if (StrUtil.isBlank(jdbcUrl)) {
            throw new IllegalArgumentException("Invalid JDBC url.");
        }

        jdbcUrl = jdbcUrl.toLowerCase();

        if (jdbcUrl.startsWith("jdbc:impala")) {
            jdbcUrl = jdbcUrl.replace(":impala", "");
        }

        if (jdbcUrl.startsWith("jdbc:p6spy")) {
            jdbcUrl = jdbcUrl.replace(":p6spy", "");
        }

        if (!jdbcUrl.startsWith("jdbc:")
            || (pos1 = jdbcUrl.indexOf(':', 5)) == -1) {
            throw new IllegalArgumentException("Invalid JDBC url.");
        }

        final String name = ReUtil.getGroup1("jdbc:(.*?):", jdbcUrl);


        if (name.contains("mysql") || name.contains("cobar")) {
            connUri = jdbcUrl.substring(pos1 + 1);

            if (connUri.startsWith("//")) {
                if ((pos = connUri.indexOf('/', 2)) != -1) {
                    database = connUri.substring(pos + 1);
                }
            } else {
                database = connUri;
            }

            assert database != null;

            if (database.contains("?")) {
                database = database.substring(0, database.indexOf("?"));
            }

            if (database.contains(";")) {
                database = database.substring(0, database.indexOf(";"));
            }
        } else if (name.contains("dm") || name.contains("sqlserver") || name.contains("microsoft")) {
            database = getSqlServerDbName(jdbcUrl);
        }

        if (StrUtil.isBlank(database)) {
            throw new IllegalArgumentException("Invalid JDBC url.");
        }
        return database;
    }

    /**
     * 获得所有表名
     *
     * @param ds 数据源
     * @return 表名列表
     */
    public static List<Table> getTables(DataSource ds) {
        final List<Table> tables = new ArrayList<>();
        Connection conn = null;
        try {
            conn = ds.getConnection();
            // catalog和schema获取失败默认使用null代替
            String catalog = MetaUtil.getCatalog(conn);
            String schema = MetaUtil.getSchema(conn);


            final DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet rs = metaData.getTables(catalog, schema, null, Convert.toStrArray(TableType.TABLE))) {
                if (null != rs) {
                    String tableName;
                    while (rs.next()) {
                        tableName = rs.getString("TABLE_NAME");
                        if (StrUtil.isNotBlank(tableName)) {
                            final Table table = Table.create(tableName);
                            table.setCatalog(catalog);
                            table.setSchema(schema);
                            table.setComment(rs.getString("REMARKS"));
                            // 获得主键
                            try (ResultSet rsPk = metaData.getPrimaryKeys(catalog, schema, tableName)) {
                                if (null != rsPk) {
                                    while (rsPk.next()) {
                                        table.addPk(rsPk.getString("COLUMN_NAME"));
                                    }
                                }
                            }
                            tables.add(table);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new DbRuntimeException("Get tables error!", e);
        } finally {
            cn.hutool.db.DbUtil.close(conn);
        }
        return tables;
    }

    /**
     * 不关闭 Connection,因为是从事务里获取的,sqlSession会负责关闭
     *
     * @param executor Executor
     * @return DbType
     */
    public static DbType getDbType(DataSource executor) {
        try {
            Connection conn = executor.getConnection();
            return JDBC_DB_TYPE_CACHE.computeIfAbsent(conn.getMetaData().getURL(), JdbcUtils::getDbType);
        } catch (SQLException e) {
            throw ExceptionUtils.mpe(e);
        }
    }

    /**
     * 根据连接地址判断数据库类型
     *
     * @param jdbcUrl 连接地址
     * @return ignore
     */
    public static DbType getDbType(String jdbcUrl) {
        Assert.isFalse(StringUtils.isBlank(jdbcUrl), "Error: The jdbcUrl is Null, Cannot read database type");
        String url = jdbcUrl.toLowerCase();
        if (url.contains(":mysql:") || url.contains(":cobar:")) {
            return DbType.MYSQL;
        } else if (url.contains(":mariadb:")) {
            return DbType.MARIADB;
        } else if (url.contains(":oracle:")) {
            return DbType.ORACLE;
        } else if (url.contains(":sqlserver:") || url.contains(":microsoft:")) {
            return DbType.SQL_SERVER2005;
        } else if (url.contains(":sqlserver2012:")) {
            return DbType.SQL_SERVER;
        } else if (url.contains(":postgresql:")) {
            return DbType.POSTGRE_SQL;
        } else if (url.contains(":hsqldb:")) {
            return DbType.HSQL;
        } else if (url.contains(":db2:")) {
            return DbType.DB2;
        } else if (url.contains(":sqlite:")) {
            return DbType.SQLITE;
        } else if (url.contains(":h2:")) {
            return DbType.H2;
        } else if (regexFind(":dm\\d*:", url)) {
            return DbType.DM;
        } else if (url.contains(":xugu:")) {
            return DbType.XU_GU;
        } else if (regexFind(":kingbase\\d*:", url)) {
            return DbType.KINGBASE_ES;
        } else if (url.contains(":phoenix:")) {
            return DbType.PHOENIX;
        } else if (url.contains(":zenith:")) {
            return DbType.GAUSS;
        } else if (url.contains(":gbase:")) {
            return DbType.GBASE;
        } else if (url.contains(":gbasedbt-sqli:") || url.contains(":informix-sqli:")) {
            return DbType.GBASE_8S;
        } else if (url.contains(":clickhouse:")) {
            return DbType.CLICK_HOUSE;
        } else if (url.contains(":oscar:")) {
            return DbType.OSCAR;
        } else if (url.contains(":sybase:")) {
            return DbType.SYBASE;
        } else if (url.contains(":oceanbase:")) {
            return DbType.OCEAN_BASE;
        } else if (url.contains(":highgo:")) {
            return DbType.HIGH_GO;
        } else if (url.contains(":cubrid:")) {
            return DbType.CUBRID;
        } else if (url.contains(":sap:")) {
            return DbType.SAP_HANA;
        } else if (url.contains(":impala:")) {
            return DbType.IMPALA;
        } else if (url.contains(":vertica:")) {
            return DbType.VERTICA;
        } else if (url.contains(":xcloud:")) {
            return DbType.XCloud;
        } else if (url.contains(":firebirdsql:")) {
            return DbType.FIREBIRD;
        } else {
            log.warn("The jdbcUrl is " + jdbcUrl + ", Mybatis Plus Cannot Read Database type or The Database's Not Supported!");
            return DbType.OTHER;
        }
    }


    /**
     * 正则匹配
     *
     * @param regex 正则
     * @param input 字符串
     * @return 验证成功返回 true，验证失败返回 false
     */
    public static boolean regexFind(String regex, CharSequence input) {
        if (null == input) {
            return false;
        }
        return Pattern.compile(regex).matcher(input).find();
    }
}

# 说明

本模块参考 uid-generator， 由于该项目没有发布正式版本，故将该项目源码复制过来方便使用。 若有侵权，联系作者删除

改动的地方：

1. WorkerNodeDAO 从 com.baidu.fsg.uid.worker.dao.WorkerNodeDAO 移动到 com.luohuo.basic.uid.dao.WorkerNodeDAO
2. DisposableWorkerIdAssigner 类的assignWorkerId方法，事务增加了：(rollbackFor = Exception.class)

参考地址： https://github.com/baidu/uid-generator

原理参考： https://www.cnblogs.com/csonezp/p/12088432.html

# 关于UID比特分配的建议

对于并发数要求不高、期望长期使用的应用, 可增加timeBits位数, 减少seqBits位数. 例如节点采取用完即弃的WorkerIdAssigner策略,
重启频率为12次/天, 那么配置成{"workerBits":23,"timeBits":31,"seqBits":9}时,
可支持28个节点以整体并发量14400 UID/s的速度持续运行68年.

对于节点重启频率频繁、期望长期使用的应用, 可增加workerBits和timeBits位数, 减少seqBits位数. 例如节点采取用完即弃的WorkerIdAssigner策略,
重启频率为24*12次/天, 那么配置成{"workerBits":27,"timeBits":30,"seqBits":6}时,
可支持37个节点以整体并发量2400 UID/s的速度持续运行34年.

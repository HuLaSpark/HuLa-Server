package com.luohuo.basic.function;

import cn.hutool.core.lang.Pair;

import java.util.Optional;
import java.util.function.Function;

/**
 * 封装异常 stream 流的异常
 * https://mp.weixin.qq.com/s/jBXYj-w0woXtPPUxGhD8Ug
 *
 * @author IT牧场
 * @author 乾乾
 * @date 2019/05/15
 */
public final class Either<L, R> {
    /**
     * 左值 表示异常
     */
    private final L left;
    /**
     * 右值表示 成功的数据
     */
    private final R right;

    private Either(L left, R right) {
        this.left = left;
        this.right = right;
    }

    private static <L, R> Either<L, R> left(L left) {
        return new Either<>(left, null);
    }

    private static <L, R> Either<L, R> right(R right) {
        return new Either<>(null, right);
    }

    /**
     * 处理类
     *
     * @param function 回调
     * @param <T>
     * @param <R>
     * @return 回调
     */
    public static <T, R> Function<T, Either> lift(CheckedFunction<T, R> function) {
        return t -> {
            try {
                return Either.right(function.apply(t));
            } catch (Exception ex) {
                return Either.left(ex);
            }
        };
    }

    public static <T, R> Function<T, Either> liftWithValue(CheckedFunction<T, R> function) {
        return t -> {
            try {
                return Either.right(function.apply(t));
            } catch (Exception ex) {
                return Either.left(Pair.of(ex, t));
            }
        };
    }

    public Optional<L> getLeft() {
        return Optional.ofNullable(left);
    }

    public Optional<R> getRight() {
        return Optional.ofNullable(right);
    }

    public boolean isLeft() {
        return left != null;
    }

    public boolean isRight() {
        return right != null;
    }

    public <T> Optional<T> mapLeft(Function<? super L, T> mapper) {
        if (isLeft()) {
            return Optional.of(mapper.apply(left));
        }
        return Optional.empty();
    }

    public <T> Optional<T> mapRight(Function<? super R, T> mapper) {
        if (isRight()) {
            return Optional.of(mapper.apply(right));
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        if (isLeft()) {
            return "Left(" + left + ")";
        }
        return "Right(" + right + ")";
    }
}

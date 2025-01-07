package org.thermoweb.intellij.plugin.encrypt;

import java.util.function.Consumer;

public sealed interface Result<T, E> {
	record Ok<T, E>(T value) implements Result<T, E> {
	}

	record Error<T, E>(E error, String errorMessage) implements Result<T, E> {
	}

	static <T, E> Result<T, E> ok(T value) {
		return new Ok<>(value);
	}

	static <T, E> Result<T, E> error(E error, String errorMessage) {
		return new Error<>(error, errorMessage);
	}

	default Result<T, E> ifSuccess(Consumer<T> action) {
		if (this instanceof Ok<T, E> ok) {
			action.accept(ok.value());
		}
		return this;
	}

	default void ifSuccessOrElse(Consumer<T> success, Consumer<E> orElse) {
		if (this instanceof Ok<T, E> ok) {
			success.accept(ok.value());
		} else if (this instanceof Error<T, E> error) {
			orElse.accept(error.error());
		} else {
			throw new IllegalArgumentException("Unknown result type: " + this);
		}
	}

	default boolean isSuccess() {
		return this instanceof Ok<T, E>;
	}

	default boolean isError() {
		return this instanceof Result.Error<T,E>;
	}
}

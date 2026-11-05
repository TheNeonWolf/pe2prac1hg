package cs2030s.fp;

/**
 * CS2030S PE2 Question 1
 * AY20/21 Semester 2
 *
 * @author A0000000X
 */
public abstract class Try<T> {
  public static <T> Try<T> of(Producer<? extends T> prod) {
    try {
      return new Success<>(prod.produce());
    } catch (Throwable exc) {
      return new Failure<>(exc);
    }
  }

  public static <T> Try<T> success(T value) {
    return new Success<T>(value);
  }

  public static <T> Try<T> failure(Throwable exc) {
    return new Failure<>(exc);
  }

  public abstract T get() throws Throwable;

  public abstract <R> Try<R> map(Transformer<? super T, ? extends R> fn);

  public abstract <R> Try<R> flatMap(Transformer<? super T, ? extends Try<? extends R>> fn);

  public abstract Try<T> onFailure(Consumer<? super Throwable> cons);

  public abstract Try<T> recover(Transformer<? super Throwable, ? extends T> fn);

  private static class Success<T> extends Try<T> {
    private T value;

    public Success(T value) {
      this.value = value;
    }

    @Override
    public T get() throws Throwable {
      return this.value;
    }

    @Override
    public <R> Try<R> map(Transformer<? super T, ? extends R> fn) {
      return Try.of(() -> fn.transform(this.value));
    }

    @Override
    public <R> Try<R> flatMap(Transformer<? super T, ? extends Try<? extends R>> fn) {
      return Try.of(() -> fn.transform(this.value).get());
    }

    @Override
    public Try<T> onFailure(Consumer<? super Throwable> cons) {
      return this;
    }

    @Override
    public Try<T> recover(Transformer<? super Throwable, ? extends T> fn) {
      return this;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj instanceof Success<?>) {
        Success<?> success = (Success<?>) obj;
        if (this.value == null) {
          return success.value == null;
        } else {
          return this.value.equals(success.value);
        }
      }
      return false;
    }
  }
  private static class Failure<T> extends Try<T> {
    private Throwable exc;

    public Failure(Throwable exc) {
      this.exc = exc;
    }

    @Override
    public T get() throws Throwable {
      throw this.exc;
    }

    @Override
    public <R> Try<R> map(Transformer<? super T, ? extends R> fn) {
      return this.self();
    }

    @Override
    public <R> Try<R> flatMap(Transformer<? super T, ? extends Try<? extends R>> fn) {
      return this.self();
    }

    @Override
    public Try<T> onFailure(Consumer<? super Throwable> cons) {
      try {
        cons.consume(this.exc);
      } catch (Throwable exc) {
        return Try.failure(exc);
      }
      return this.self();
    }

    @Override
    public Try<T> recover(Transformer<? super Throwable, ? extends T> fn) {
      return Try.of(() -> fn.transform(this.exc));
    }

    /**
     * Used to combine all the @SuppressWarnings into one location
     */
    private <R> Try<R> self() {
      @SuppressWarnings("unchecked")
      Try<R> res = (Try<R>) this;
      return res;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj instanceof Failure<?>) {
        Failure<?> failure = (Failure<?>) obj;
        return this.exc.toString().equals(failure.exc.toString());
      }
      return false;
    }
  }
}
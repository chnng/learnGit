package com.learn.git.api.eventbus;

public class MessageEvent<T> {
    public int what;
    public int arg1;
    public boolean arg2;
    public T obj;

    private MessageEvent(Builder<T> builder) {
        what = builder.what;
        arg1 = builder.arg1;
        arg2 = builder.arg2;
        obj = builder.obj;
    }

    @Override
    public String toString() {
        return "MessageEvent{" +
                "what=" + what +
                ", arg1=" + arg1 +
                ", arg2=" + arg2 +
                ", obj=" + obj +
                '}';
    }

    public static final class Builder<T> {
        private int what;
        private int arg1;
        private boolean arg2;
        private T obj;

        public Builder<T> what(int val) {
            what = val;
            return this;
        }

        public Builder<T> arg1(int val) {
            arg1 = val;
            return this;
        }

        public Builder<T> arg2(boolean val) {
            arg2 = val;
            return this;
        }

        public Builder<T> obj(T val) {
            obj = val;
            return this;
        }

        public MessageEvent<T> build() {
            return new MessageEvent<T>(this);
        }
    }
}

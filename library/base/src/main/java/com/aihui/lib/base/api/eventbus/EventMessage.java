package com.aihui.lib.base.api.eventbus;

/**
 * Created by 路传涛 on 2017/5/27.
 * 接受消息的地方根据HashMap键名取值
 * <p>
 * <p>
 * 接收消息页面的四个方法说明：
 * 1.ThreadMode: POSTING
 * 如果使用onEvent作为订阅函数，那么该事件在哪个线程发布出来的，onEvent就会在这个线程中运行，
 * 也就是说发布事件和接收事件线程在同一个线程。使用这个方法时，在onEvent方法中不能执行耗时操作，
 * 如果执行耗时操作容易导致事件分发延迟
 * // Called in the same thread (default)
 * // ThreadMode is optional here
 *
 * @Subscribe(threadMode = ThreadMode.POSTING)
 * public void onMessage(EventMessage event) {
 * log(event.message);
 * }
 * <p>
 * 2.ThreadMode: MAIN
 * 如果使用onEventMainThread作为订阅函数，那么不论事件是在哪个线程中发布出来的，
 * onEventMainThread都会在UI线程中执行，接收事件就会在UI线程中运行，
 * 这个在Android中是非常有用的，因为在Android中只能在UI线程中跟新UI，
 * 所以在onEvnetMainThread方法中是不能执行耗时操作的。
 * <p>
 * // Called in Android UI's main thread
 * @Subscribe(threadMode = ThreadMode.MAIN)
 * public void onMessage(EventMessage event) {
 * textField.setText(event.message);
 * }
 * <p>
 * 3.ThreadMode: BACKGROUND
 * 如果使用onEventBackgrond作为订阅函数，那么如果事件是在UI线程中发布出来的，
 * 那么onEventBackground就会在子线程中运行，如果事件本来就是子线程中发布出来的，
 * 那么onEventBackground函数直接在该子线程中执行。
 * <p>
 * // Called in the background thread
 * @Subscribe(threadMode = ThreadMode.BACKGROUND)
 * public void onMessage(EventMessage event){
 * saveToDisk(event.message);
 * }
 * <p>
 * 4.ThreadMode: ASYNC
 * 使用这个函数作为订阅函数，那么无论事件在哪个线程发布，都会创建新的子线程在执行
 * <p>
 * // Called in a separate thread
 * @Subscribe(threadMode = ThreadMode.ASYNC)
 * public void onMessage(EventMessage event){
 * backend.send(event.message);
 * }
 */

public class EventMessage<T> {
    private int key;
    private T value;

    public EventMessage(int key) {
        this.key = key;
    }

    public EventMessage(int key, T value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}

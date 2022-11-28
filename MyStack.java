import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class MyStack<T> extends Stack<T> {
    public List<T> pop(Integer size){
        LinkedList<T> list = new LinkedList<>();
        for (int i = 0; i <size; i++) {
            list.addFirst( this.pop());
        }
        return list;
    }

    public List<T> popReverted(Integer size)
    {  LinkedList<T> list = new LinkedList<>();
        for (int i = 0; i <size; i++) {
            list.add( this.pop());
        }
        return list;
    }
}

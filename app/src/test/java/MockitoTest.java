import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by VIanoshchuk on 17.03.2015.
 */
public class MockitoTest  {

    @Test
    public void Test2(){
        List mockedList = mock(List.class);
        //используем его
        mockedList.add("one");
        mockedList.clear();

        //проверяем, были ли вызваны методы add с параметром "one" и clear
        verify(mockedList).add("one");
        verify(mockedList).clear();

    }
    @Test
    public void Test4(){
        List list = new LinkedList();
        List spy = spy(list);

//опционально, определяем лишь метод size()
        when(spy.size()).thenReturn(100);

//используем реальные методы
        spy.add("one");
        spy.add("two");

//получим "one"
        System.out.println(spy.get(0));

//метод size() нами переопределён - получим 100
        System.out.println(spy.size());

//можем проверить
        verify(spy).add("one");
        verify(spy).add("two");
    }

    @Test
    public void Test3(){
        LinkedList mockedList = mock(LinkedList.class);

//stub'инг
        when(mockedList.get(0)).thenReturn("first");
        when(mockedList.get(1)).thenThrow(new RuntimeException());

//получим "first"
        System.out.println(mockedList.get(0));


//получим RuntimeException
//        System.out.println(mockedList.get(1));

//получим "null" ибо get(999) не был определен
        System.out.println(mockedList.get(999));


        //используем mock-объект
        mockedList.add("once");

        mockedList.add("twice");
        mockedList.add("twice");

        mockedList.add("three times");
        mockedList.add("three times");
        mockedList.add("three times");
        mockedList.add("five times");
        mockedList.add("five times");
       // mockedList.add("never happened");

//по умолчанию проверка, что вызывался 1 раз ~ times(1)
        verify(mockedList).add("once");
        verify(mockedList, times(1)).add("once");

//точное число вызовов
        verify(mockedList, times(2)).add("twice");
        verify(mockedList, times(3)).add("three times");

//никогда ~ never() ~ times(0)
        verify(mockedList, never()).add("never happened");

//как минимум, как максимум
        verify(mockedList, atLeastOnce()).add("three times");
        verify(mockedList, atLeast(2)).add("five times");
        verify(mockedList, atMost(5)).add("three times");
        
    }
    // assume there is a class MyDatabase
    @Test
    public void iterator_will_return_hello_world() {
        //подготавливаем
        Iterator i = mock(Iterator.class);
        when(i.next()).thenReturn("Hello").thenReturn("World");
        //выполняем
        String result = i.next()+" "+i.next();
        //сравниваем
        assertEquals("Hello World", result);
    }

    @Test
    public void with_arguments() {
        Comparable c = mock(Comparable.class);
        when(c.compareTo("Test")).thenReturn(1);
        assertEquals(1, c.compareTo("Test"));
    }
    @Test
    public void with_unspecified_arguments() {
        Comparable c = mock(Comparable.class);
        when(c.compareTo(anyInt())).thenReturn(-1);
        assertEquals(-1, c.compareTo(5));
    }

    @Test(expected=IOException.class)
    public void OutputStreamWriter_rethrows_an_exception_from_OutputStream()
            throws IOException {
        OutputStream mock = mock(OutputStream.class);
        OutputStreamWriter osw = new OutputStreamWriter(mock);
        doThrow(new IOException()).when(mock).close();
        osw.close();
    }
    @Test
    public void OutputStreamWriter_Closes_OutputStream_on_Close()
            throws IOException {
        OutputStream mock = mock(OutputStream.class);
        OutputStreamWriter osw = new OutputStreamWriter(mock);
        osw.close();
        verify(mock).close();
    }

    @Test
    public void OutputStreamWriter_Buffers_And_Forwards_To_OutputStream()
            throws IOException {
        OutputStream mock = mock(OutputStream.class);
        OutputStreamWriter osw = new OutputStreamWriter(mock);
        osw.write('a');
        osw.flush();
        // не можем делать так, потому что мы не знаем,
        // насколько длинным может быть массив
        // verify(mock).write(new byte[]{'a'}, 0, 1);

        BaseMatcher<byte[]> arrayStartingWithA = new BaseMatcher<byte[]>() {
            @Override
            public void describeTo(Description description) {
                // пустота
            }
            // Проверяем, что первый символ - это A
            @Override
            public boolean matches(Object item) {
                byte[] actual = (byte[]) item;
                return actual[0] == 'a';
            }
        };
        // проверяем, что первый символ массива - это A, и что другие два аргумента равны 0 и 1.
        verify(mock).write(argThat(arrayStartingWithA), eq(0), eq(1));
    }

/*
    @Test
    public void TestDBAdapter(){
        MockContext context = new MockContext();

        DatabaseAdapter dba = new DatabaseAdapter(context);

        dba.
        //List list = new LinkedList();
//        DatabaseAdapter spy = spy(dba);

//опционально, определяем лишь метод size()
       // when(spy.size()).thenReturn(100);


  } */

}
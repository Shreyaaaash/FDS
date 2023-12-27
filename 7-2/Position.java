import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


interface Position<E> {
    E getElement() throws IllegalStateException;
}
package com.snypir.callback.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by stepangoncarov on 17/06/14.
 */
@Table(name = "my_numbers")
public class MyNumber extends Model {

    public static final String NUMBER = "number";
    public static final String FLAG = "flag";
    public static final String COUNTRY = "country";

    @Column(name = NUMBER, unique = true, onUniqueConflict = Column.ConflictAction.IGNORE)
    String number;

    @Column(name = FLAG)
    String flag;

    @Column(name = COUNTRY)
    String country;

    public MyNumber() {
    }

    private MyNumber(final String number, final String flag) {
        this.number = number;
        this.flag = flag;
    }

    public String getNumber() {
        return number;
    }

    @NonNull
    public static MyNumber instantiatePrimary(@NonNull final String number){
        return new MyNumber(number, FLAGS.PRIMARY);
    }

    @NonNull
    public static MyNumber instantiateSecondary(@NonNull final String number){
        return new MyNumber(number, FLAGS.SECONDARY);
    }

    @Nullable
    public static List<MyNumber> getAll() {
        return new Select()
                .from(MyNumber.class)
                .execute();
    }

    @Nullable
    public static MyNumber getPrimary(){
        return new Select()
                .from(MyNumber.class)
                .where(FLAG + "=?", FLAGS.PRIMARY)
                .executeSingle();
    }

    @Nullable
    public static List<MyNumber> getSecondary(){
        return new Select()
                .from(MyNumber.class)
                .where(FLAG + "=?", FLAGS.SECONDARY)
                .execute();
    }


    public static void deleteAll() {
        new Delete().from(MyNumber.class).execute();
    }

    public interface FLAGS {
        String PRIMARY = "primary";
        String SECONDARY = "secondary";
    }

}

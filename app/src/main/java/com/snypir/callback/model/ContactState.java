package com.snypir.callback.model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by stepangoncarov on 03/07/14.
 */
@Table(name = "contact_state", id = BaseColumns._ID)
public class ContactState extends Model {

    @Column(name = "data_version")
    int dataVersion;
    @Column(name = "contact_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    long contactId;

    public ContactState() {
    }

    public int getDataVersion() {
        return dataVersion;
    }

    public long getContactId() {
        return contactId;
    }

    public ContactState(long contactId, int dataVersion) {
        this.dataVersion = dataVersion;
        this.contactId = contactId;
    }

    public static List<ContactState> getAll(){
        return new Select().from(ContactState.class).execute();
    }
}

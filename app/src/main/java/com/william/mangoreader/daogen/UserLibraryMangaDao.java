package com.william.mangoreader.daogen;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "USER_LIBRARY_MANGA".
*/
public class UserLibraryMangaDao extends AbstractDao<UserLibraryManga, String> {

    public static final String TABLENAME = "USER_LIBRARY_MANGA";

    /**
     * Properties of entity UserLibraryManga.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property MangaEdenId = new Property(0, String.class, "MangaEdenId", true, "MANGA_EDEN_ID");
        public final static Property Tab = new Property(1, String.class, "tab", false, "TAB");
        public final static Property Title = new Property(2, String.class, "title", false, "TITLE");
        public final static Property ImageURL = new Property(3, String.class, "imageURL", false, "IMAGE_URL");
        public final static Property Status = new Property(4, String.class, "status", false, "STATUS");
        public final static Property LastChapterDate = new Property(5, Long.class, "lastChapterDate", false, "LAST_CHAPTER_DATE");
        public final static Property Hits = new Property(6, Integer.class, "hits", false, "HITS");
    };


    public UserLibraryMangaDao(DaoConfig config) {
        super(config);
    }
    
    public UserLibraryMangaDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"USER_LIBRARY_MANGA\" (" + //
                "\"MANGA_EDEN_ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: MangaEdenId
                "\"TAB\" TEXT," + // 1: tab
                "\"TITLE\" TEXT NOT NULL ," + // 2: title
                "\"IMAGE_URL\" TEXT," + // 3: imageURL
                "\"STATUS\" TEXT," + // 4: status
                "\"LAST_CHAPTER_DATE\" INTEGER," + // 5: lastChapterDate
                "\"HITS\" INTEGER);"); // 6: hits
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"USER_LIBRARY_MANGA\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, UserLibraryManga entity) {
        stmt.clearBindings();
 
        String MangaEdenId = entity.getMangaEdenId();
        if (MangaEdenId != null) {
            stmt.bindString(1, MangaEdenId);
        }

        String tab = entity.getTab();
        if (tab != null) {
            stmt.bindString(2, tab);
        }
        stmt.bindString(3, entity.getTitle());
 
        String imageURL = entity.getImageURL();
        if (imageURL != null) {
            stmt.bindString(4, imageURL);
        }
 
        String status = entity.getStatus();
        if (status != null) {
            stmt.bindString(5, status);
        }
 
        Long lastChapterDate = entity.getLastChapterDate();
        if (lastChapterDate != null) {
            stmt.bindLong(6, lastChapterDate);
        }
 
        Integer hits = entity.getHits();
        if (hits != null) {
            stmt.bindLong(7, hits);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public UserLibraryManga readEntity(Cursor cursor, int offset) {
        UserLibraryManga entity = new UserLibraryManga( //
                cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // MangaEdenId
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // tab
                cursor.getString(offset + 2), // title
                cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // imageURL
                cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // status
                cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // lastChapterDate
                cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6) // hits
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, UserLibraryManga entity, int offset) {
        entity.setMangaEdenId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setTab(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTitle(cursor.getString(offset + 2));
        entity.setImageURL(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setStatus(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setLastChapterDate(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setHits(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(UserLibraryManga entity, long rowId) {
        return entity.getMangaEdenId();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(UserLibraryManga entity) {
        if(entity != null) {
            return entity.getMangaEdenId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}

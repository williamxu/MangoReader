package com.william.mangoreader.daogen;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.william.mangoreader.daogen.UserLibraryManga;

import com.william.mangoreader.daogen.UserLibraryMangaDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig userLibraryMangaDaoConfig;

    private final UserLibraryMangaDao userLibraryMangaDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        userLibraryMangaDaoConfig = daoConfigMap.get(UserLibraryMangaDao.class).clone();
        userLibraryMangaDaoConfig.initIdentityScope(type);

        userLibraryMangaDao = new UserLibraryMangaDao(userLibraryMangaDaoConfig, this);

        registerDao(UserLibraryManga.class, userLibraryMangaDao);
    }
    
    public void clear() {
        userLibraryMangaDaoConfig.getIdentityScope().clear();
    }

    public UserLibraryMangaDao getUserLibraryMangaDao() {
        return userLibraryMangaDao;
    }

}

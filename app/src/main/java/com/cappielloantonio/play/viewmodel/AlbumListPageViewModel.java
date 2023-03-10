package com.cappielloantonio.play.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cappielloantonio.play.model.Album;
import com.cappielloantonio.play.repository.AlbumRepository;
import com.cappielloantonio.play.repository.DownloadRepository;
import com.cappielloantonio.play.subsonic.models.AlbumID3;
import com.cappielloantonio.play.subsonic.models.ArtistID3;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

public class AlbumListPageViewModel extends AndroidViewModel {
    private final AlbumRepository albumRepository;
    private final DownloadRepository downloadRepository;

    public String title;
    public ArtistID3 artist;

    private MutableLiveData<List<AlbumID3>> albumList;

    public AlbumListPageViewModel(@NonNull Application application) {
        super(application);

        albumRepository = new AlbumRepository();
        downloadRepository = new DownloadRepository();
    }

    public LiveData<List<AlbumID3>> getAlbumList(LifecycleOwner owner) {
        albumList = new MutableLiveData<>(new ArrayList<>());

        switch (title) {
            case Album.RECENTLY_PLAYED:
                albumRepository.getAlbums("recent", 500, null, null).observe(owner, albums -> albumList.setValue(albums));
                break;
            case Album.MOST_PLAYED:
                albumRepository.getAlbums("frequent", 500, null, null).observe(owner, albums -> albumList.setValue(albums));
                break;
            case Album.RECENTLY_ADDED:
                albumRepository.getAlbums("newest", 500, null, null).observe(owner, albums -> albumList.setValue(albums));
                break;
            case Album.STARRED:
                albumList = albumRepository.getStarredAlbums(false, -1);
                break;
            case Album.NEW_RELEASES:
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                albumRepository.getAlbums("byYear", 500, currentYear, currentYear).observe(owner, albums -> {
                    albums.sort(Comparator.comparing(AlbumID3::getCreated).reversed());
                    albumList.postValue(albums.subList(0, Math.min(20, albums.size())));
                });
                break;
            case Album.DOWNLOADED:
                // TODO
                // downloadRepository.getLiveDownload().observe(owner, downloads -> albumList.setValue(MappingUtil.mapDownloadToAlbum(downloads)));
                break;
            case Album.FROM_ARTIST:
                // TODO
                // downloadRepository.getLiveDownloadFromArtist(artist.getId()).observe(owner, downloads -> albumList.setValue(MappingUtil.mapDownloadToAlbum(downloads)));
                break;
        }

        return albumList;
    }
}

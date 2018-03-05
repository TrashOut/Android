// IRemoteService.aidl
package me.trashout.service;

import me.trashout.service.ITrashHunterChangeListener;

interface IRemoteService {
    void addOnTrashHunterChangeListener(ITrashHunterChangeListener listener);
    void removeOnTrashHunterChangeListener(ITrashHunterChangeListener listener);
}

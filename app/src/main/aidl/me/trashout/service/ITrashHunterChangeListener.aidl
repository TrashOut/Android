// ITrashHunterChangeListener.aidl
package me.trashout.service;

interface ITrashHunterChangeListener {
       void onTrashHunterStateChange();
       void onTrashHunterTrashCountChange(int trashCount);
}

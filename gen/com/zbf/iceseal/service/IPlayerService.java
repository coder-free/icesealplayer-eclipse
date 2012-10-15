/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: G:\\MySrc\\Eclipse\\new_work\\IceSeal\\src\\com\\zbf\\iceseal\\service\\IPlayerService.aidl
 */
package com.zbf.iceseal.service;
public interface IPlayerService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.zbf.iceseal.service.IPlayerService
{
private static final java.lang.String DESCRIPTOR = "com.zbf.iceseal.service.IPlayerService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.zbf.iceseal.service.IPlayerService interface,
 * generating a proxy if needed.
 */
public static com.zbf.iceseal.service.IPlayerService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.zbf.iceseal.service.IPlayerService))) {
return ((com.zbf.iceseal.service.IPlayerService)iin);
}
return new com.zbf.iceseal.service.IPlayerService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_changeState:
{
data.enforceInterface(DESCRIPTOR);
this.changeState();
return true;
}
case TRANSACTION_next:
{
data.enforceInterface(DESCRIPTOR);
this.next();
return true;
}
case TRANSACTION_last:
{
data.enforceInterface(DESCRIPTOR);
this.last();
return true;
}
case TRANSACTION_seekTo:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.seekTo(_arg0);
return true;
}
case TRANSACTION_changeMode:
{
data.enforceInterface(DESCRIPTOR);
this.changeMode();
return true;
}
case TRANSACTION_setVolume:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.setVolume(_arg0, _arg1);
return true;
}
case TRANSACTION_playThis:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
java.lang.String _arg2;
_arg2 = data.readString();
this.playThis(_arg0, _arg1, _arg2);
return true;
}
case TRANSACTION_broadcastSongChange:
{
data.enforceInterface(DESCRIPTOR);
this.broadcastSongChange();
return true;
}
case TRANSACTION_stopSelf:
{
data.enforceInterface(DESCRIPTOR);
this.stopSelf();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.zbf.iceseal.service.IPlayerService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void changeState() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_changeState, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
public void next() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_next, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
public void last() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_last, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
public void seekTo(int msec) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(msec);
mRemote.transact(Stub.TRANSACTION_seekTo, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
public void changeMode() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_changeMode, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
public void setVolume(int leftVolume, int rightVolume) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(leftVolume);
_data.writeInt(rightVolume);
mRemote.transact(Stub.TRANSACTION_setVolume, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
public void playThis(int position, int listType, java.lang.String listParamete) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(position);
_data.writeInt(listType);
_data.writeString(listParamete);
mRemote.transact(Stub.TRANSACTION_playThis, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
public void broadcastSongChange() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_broadcastSongChange, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
public void stopSelf() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopSelf, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_changeState = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_next = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_last = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_seekTo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_changeMode = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_setVolume = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_playThis = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_broadcastSongChange = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_stopSelf = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
}
public void changeState() throws android.os.RemoteException;
public void next() throws android.os.RemoteException;
public void last() throws android.os.RemoteException;
public void seekTo(int msec) throws android.os.RemoteException;
public void changeMode() throws android.os.RemoteException;
public void setVolume(int leftVolume, int rightVolume) throws android.os.RemoteException;
public void playThis(int position, int listType, java.lang.String listParamete) throws android.os.RemoteException;
public void broadcastSongChange() throws android.os.RemoteException;
public void stopSelf() throws android.os.RemoteException;
}

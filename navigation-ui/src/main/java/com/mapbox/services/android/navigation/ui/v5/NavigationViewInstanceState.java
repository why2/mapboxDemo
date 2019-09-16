package com.mapbox.services.android.navigation.ui.v5;

import android.os.Parcel;
import android.os.Parcelable;

class NavigationViewInstanceState implements Parcelable {

  private int recenterButtonVisibility;
  private boolean instructionViewVisible;
  private boolean isMuted;

  NavigationViewInstanceState(int recenterButtonVisibility,
                              boolean instructionViewVisible,
                              boolean isMuted) {
    this.recenterButtonVisibility = recenterButtonVisibility;
    this.instructionViewVisible = instructionViewVisible;
    this.isMuted = isMuted;
  }

  int getRecenterButtonVisibility() {
    return recenterButtonVisibility;
  }

  boolean isInstructionViewVisible() {
    return instructionViewVisible;
  }


  boolean isMuted() {
    return isMuted;
  }

  private NavigationViewInstanceState(Parcel in) {
    recenterButtonVisibility = in.readInt();
    instructionViewVisible = in.readByte() != 0;
    isMuted = in.readByte() != 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(recenterButtonVisibility);
    dest.writeByte((byte) (instructionViewVisible ? 1 : 0));
    dest.writeByte((byte) (isMuted ? 1 : 0));
  }

  @Override
  public int describeContents() {
    return 0;
  }

  public static final Creator<NavigationViewInstanceState> CREATOR = new Creator<NavigationViewInstanceState>() {
    @Override
    public NavigationViewInstanceState createFromParcel(Parcel in) {
      return new NavigationViewInstanceState(in);
    }

    @Override
    public NavigationViewInstanceState[] newArray(int size) {
      return new NavigationViewInstanceState[size];
    }
  };
}

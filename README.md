# MMSKeeper
### Android app to switch data on/off while still allowing MMS traffic

```
Copyright 2014 Bence Béky

This file is part of MMSKeeper.

MMSKeeper is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

MMSKeeper is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with MMSKeeper.  If not, see <http://www.gnu.org/licenses/>.
```

## Description

This app modifies the TYPE field of the current APN (Access Point Name) to
allow or disallow data traffic, while still being able to send and receive
multimedia messages (MMS). The desired TYPE values are customizable. Data
still needs to be enabled for MMS.

The only reason to use this app is to save on data traffic, e.g. if you have a
plan with limited data.

This app also provides a widget for easy toggle of data.

This app needs to be a system app on Android 4.0 and higher, because the
permission WRITE_APN_SETTINGS is only granted for system apps.

The easiest way to install this app is through the [F-Droid repository](https://f-droid.org/repository/browse/?fdfilter=MMSKeeper&fdid=edu.harvard.android.mmskeeper).

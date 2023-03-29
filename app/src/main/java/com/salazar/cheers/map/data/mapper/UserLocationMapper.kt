package com.salazar.cheers.map.data.mapper

import cheers.location.v1.Location
import com.salazar.cheers.map.domain.models.UserLocation


fun Location.toUserLocation(): UserLocation {
    return UserLocation(
        id = userId,
        picture = picture,
        name = name,
        username = username,
        latitude = latitude,
        longitude = longitude,
        verified = verified,
        lastUpdated = lastUpdated,
        locationName = locationName,
    )
}
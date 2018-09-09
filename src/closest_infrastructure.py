from math import sqrt, sin, cos, atan2, radians

from infrastructure import Infrastructure


def closest(lat, lon, n_samples):
    df = Infrastructure.get_instance().get_all_data().copy()

    df["distance"] = df.apply(
        lambda row: haversine_distance(lat, lon, row["lat"], row["lon"]),
        axis=1
    )
    df.sort_values("distance", inplace=True)

    return df.groupby("type").head(n_samples)


def haversine_distance(lat0, lon0, lat1, lon1):
    lat0 = radians(lat0)
    lon0 = radians(lon0)
    lat1 = radians(lat1)
    lon1 = radians(lon1)

    dlat = lat1 - lat0
    dlon = lon1 - lon0

    a = (sin(dlat / 2) ** 2) + cos(lat0) * cos(lat1) * (sin(dlon / 2) ** 2)
    c = 2 * atan2(sqrt(a), sqrt(1 - a))

    R = 6378100
    return R * c


if __name__ == "__main__":
    print(closest(49.250893, -123.124089, 3))

import pandas as pd

from collisions import Collisions


def predictor(lat, lon, severity):
    collisions = Collisions.get_instance()
    cyl_clf = collisions.cyl_clf
    ped_clf = collisions.ped_clf

    df = pd.DataFrame(
        data=[[lat, lon, collisions.encode_severity(severity)]],
        columns=["lat", "lon", "severity"]
    )

    y_cyl_pred = cyl_clf.predict(df)
    y_ped_pred = ped_clf.predict(df)

    return y_cyl_pred[0], y_ped_pred[0]


if __name__ == "__main__":
    print(predictor(49.250893, -123.124089, "Severe"))

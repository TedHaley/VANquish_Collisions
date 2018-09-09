import json
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsClassifier


class Collisions:
    _instance = None

    @staticmethod
    def get_instance():
        if Collisions._instance is None:
            Collisions._instance = Collisions()
        return Collisions._instance

    def __init__(self):
        with open("./data/dbscan_labels.json", "r") as file:
            _list = json.load(file)

        self.df = pd.DataFrame(_list)

        self.cyl_clf = None
        self.ped_clf = None
        self._train_classifiers()

    def _train_classifiers(self):
        X = self.df.loc[:, ["lat", "lon", "severity"]]
        X["severity"] = X["severity"].apply(self.encode_severity)

        y_cyl = self.df.loc[:, "cyl"]
        y_ped = self.df.loc[:, "ped"]

        X_train, X_test, y_cyl_train, y_cyl_test, y_ped_train, y_ped_test \
            = train_test_split(X, y_cyl, y_ped, test_size=0.2)

        self.cyl_clf = KNeighborsClassifier(n_neighbors=5)
        self.cyl_clf.fit(X_train, y_cyl_train)

        self.ped_clf = KNeighborsClassifier(n_neighbors=5)
        self.ped_clf.fit(X_train, y_ped_train)

    @staticmethod
    def encode_severity(severity):
        if severity == "Minor":
            return 0
        else:
            assert severity == "Severe"
            return 1

import pandas as pd


class Infrastructure:
    _instance = None

    @staticmethod
    def get_instance():
        if Infrastructure._instance is None:
            Infrastructure._instance = Infrastructure()
        return Infrastructure._instance

    def __init__(self):
        self.four_way_stops = self._load_data("./data/4_Way_Stop.xlsx")
        self.crosswalks = self._load_data("./data/Crosswalks.xlsx")
        self.curb_bulges = self._load_data("./data/Curb Bulges.xlsx", fu=True)
        self.diverters = self._load_data("./data/Diverters.xlsx")
        self.speed_humps = self._load_data("./data/SpeeedHumps.xlsx")
        self.traffic_circles = self._load_data("./data/Traffic_Circles.xlsx")
        self.traffic_signals = self._load_data("./data/Traffic Signals.xlsx")

    @staticmethod
    def _load_data(filename, fu=False):
        df = pd.read_excel(filename)

        if fu:
            df = df.loc[:, ["Lat ", "Long"]]
            df.rename(columns={"Lat ": "lat", "Long": "lon"}, inplace=True)
        else:
            df = df.loc[:, ["Lat", "Long"]]
            df.rename(columns={"Lat": "lat", "Long": "lon"}, inplace=True)

        df.dropna(inplace=True)
        return df

    def get_all_data(self):
        dfs = [
            self.four_way_stops, self.crosswalks, self.curb_bulges,
            self.diverters, self.speed_humps, self.traffic_circles,
            self.traffic_signals
        ]

        types = [
            "4-way stop", "Crosswalk", "Curb bulge", "Diverter", "Speed hump",
            "Traffic circle", "Traffic signal"
        ]

        result = []
        for df, _type in zip(dfs, types):
            temp = df.copy()
            temp.insert(0, "type", _type)

            result.append(temp)

        return pd.concat(result)

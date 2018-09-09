# -*- coding: utf-8 -*-
# Access test website by running this script using the command inline
# Visit http://127.0.0.1:8050/ to connect to the dashboard locally.

# DASH packages
import dash
import dash_core_components as dcc
import dash_html_components as html
import dash_table_experiments as dt

# packages for plots and interactions
from plotly import graph_objs as go
from plotly.graph_objs import *
from dash.dependencies import Input, Output, State, Event

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from math import sqrt, sin, cos, atan2, radians
from infrastructure import Infrastructure

from sklearn.neighbors import KNeighborsClassifier
from sklearn.model_selection import train_test_split
from sklearn.grid_search import GridSearchCV
from sklearn import preprocessing

from geopy.geocoders import Nominatim

geolocator = Nominatim(user_agent="Urban Analytics")

# Import Modules
from predictor import predictor
from closest_infrastructure import closest

import json

with open("./data/dbscan_labels.json", "r") as file:
    _list = json.load(file)
json_df = pd.DataFrame(_list)
json_df.rename(columns={'label': 'Modes', 'severity':'Injury Type', 'lat':'Latitude', 'lon':'Longitude'}, inplace=True)

# Import the data
injury_df = pd.read_excel('../data/Collision Data/Detailed collision data/VGH Injury Data/VGH 2008-2017.xlsx')

modes = injury_df["Modes"].unique()
modes = modes[~pd.isna(modes)]

cyl_ped = []
for mode in modes:
    if ('Cyl'in mode or 'Ped' in mode):
        cyl_ped.append(mode)

# Get the ped and cyl rows
injury_filt = injury_df[(injury_df["Modes"].isin(cyl_ped))]

# Bin the modes
ped_all = {"Veh-Ped", "Ped-Unk", "Mot-Ped", "Single Ped"}
cyl_all = {"Single Cyl", "Veh-Cyl", "Cyl-Unk", "Cyl-Cyl", "Mot-Cyl"}

def _bin(mode):
    if mode == "Cyl-Ped":
        return "Cyl-Ped"
    elif mode in ped_all:
        return "Ped-All"
    else:
        assert mode in cyl_all
        return "Cyl-All"

df_binned = injury_filt
df_binned["Modes"] = df_binned["Modes"].apply(_bin)

available_modes = df_binned["Modes"].unique()
available_severity = df_binned["Injury Type"].unique()

df = json_df

app = dash.Dash()
server = app.server
app.title = 'Urban Analytics'

app.layout = html.Div([

    html.H1(children='Urban Analytics'),
    html.H3(children='Severity of injury:'),
    dcc.RadioItems(
        id='injury-radio',
        options=[
            {'label': 'Severe and Minor Injuries', 'value': 1},
            {'label': 'Severe Injury', 'value': 'Severe'},
            {'label': 'Minor Injury', 'value': 'Minor'}
            ],
        value=1
    ),
    dcc.Graph(id='graph-with-radio',
            style={'height': 650,'width': 650}),
    html.H2(children=''),
    html.H3(children='Safety Recommendations for Address:'),
    dcc.Input(id='my-id', value='2050 Trutch Street, Vancouver, BC, Canada', type='text'),
    html.Div(id='my-div')
])


@app.callback(
    dash.dependencies.Output('graph-with-radio', 'figure'),
    [dash.dependencies.Input('injury-radio', 'value')])

def update_figure(selected_injury):
    if selected_injury == 'Severe' or selected_injury == 'Minor':
        filtered_df = df[df['Injury Type'] == selected_injury]
    else:
        filtered_df = df
    traces = []
    for i in filtered_df['Modes'].unique():
        df_by_mode = filtered_df[filtered_df['Modes'] == i]
        traces.append(go.Scatter(
            x=df_by_mode['Longitude'],
            y=df_by_mode['Latitude'],
            text=df_by_mode['Injury Type'],
            mode='markers',
            opacity=0.7,
            marker={
                'size': 10,
                'line': {'width': 0.5, 'color': 'white'}
            },
            name=int(i)
        ))

    return {
        'data': traces,
        'layout': go.Layout(
            xaxis={'title': 'Longitude'},
            yaxis={'title': 'Latitude'},
            margin={'l': 0, 'b': 0, 't': 0, 'r': 0},
            legend={'x': 0, 'y': 1},
            hovermode='closest',
            showlegend=False
        )
    }

@app.callback(
    Output(component_id='my-div', component_property='children'),
    [Input(component_id='my-id', component_property='value')]
)
def update_output_div(input_value):
    location = geolocator.geocode(input_value)
    if location is None:
        return [html.P("Not found!")]

    prediction = predictor(lat = location.latitude, lon = location.longitude, severity = "Severe")
    closest_infrastructure = closest(lat = location.latitude, lon = location.longitude, n_samples = 1)

    investor_str = '''  Company name: {0}
                        Number of employees: {1}
                        Main product: {2}'''.format(
    location.longitude,
    location.latitude,
    prediction[0]
    )

    return [
        html.P(f"Longitude: {location.longitude}"),
        html.P(f"Latitude: {location.latitude}"),
        html.P(f"Cyclists at risk? {prediction[0]}"),
        html.P(f"Pedestrians at risk? {prediction[1]}"),
        html.P("Closest infrastructure:"),
        dataframe_to_table(closest_infrastructure)
    ]

def dataframe_to_table(df):
    return html.Table([
        html.Tr([
            html.Td("Type"),
            html.Td("Latitude"),
            html.Td("Longitude"),
            html.Td("Distance (meters)")
        ])
    ] + [row_to_html(row) for _, row in df.iterrows()])


def row_to_html(row):
    return html.Tr([
        html.Td(row["type"]),
        html.Td(row["lat"]),
        html.Td(row["lon"]),
        html.Td(row["distance"])
    ])


if __name__ == '__main__':
    app.run_server(debug=True)

<template>
    <div class="hello">
        <v-toolbar>
            <v-toolbar-title>Bus Locator</v-toolbar-title>
            <v-btn href="/index.html" color="success" text>
                <span>Chat App</span>
            </v-btn>
        </v-toolbar>

        <v-container fluid class="font-md">
            <v-row >
                <v-col>
                    <v-card >
                        <v-card-title primary-title class="justify-center">
                            Welcome to Bus Locator
                        </v-card-title>
                        <v-row justify="center">
                            <div id="b-form">
                                <v-form method="post" v-on:submit.prevent="onSubmit" autocomplete="off">
                                    <v-text-field dense rounded filled
                                                  v-model="query"
                                                  v-on:keyup="search"
                                                  name="search-user"
                                                  placeholder="Find a bus by its number. ie. 108"
                                                  hide-details
                                                  append-icon="mdi-magnify">
                                    </v-text-field>
                                </v-form>
                            </div>
                        </v-row>
                        <v-card-text>
                            To start, you must search for a bus by its route i.e 159.
                            By selecting individual bus from the table below, you can find its real time location
                            on the map.
                        </v-card-text>
                    </v-card>

                    <div class="mt-5" id="b-data">
                        <v-data-table
                                :headers="headers"
                                :items="buses"
                                :items-per-page="5"
                                @click:row="selectRow"
                                class="elevation-1"
                        ></v-data-table>
                    </div>
                </v-col>
                <v-col cols="12" sm="6">
                    <v-card outlined>
                        <div id="b-map">
                        </div>
                    </v-card>
                </v-col>
            </v-row>
        </v-container>
    </div>
</template>

<script>
module.exports = {
    name: '',
    data () {
        return {
            mylocation: {
                lat: 40.785091,
                lng: -73.968285
            },
            map: null,
            infoWindow: null,
            marker: null,
            path: null,
            query: '',
            headers: [
                {
                    text: 'Bus Id',
                    sortable: false,
                    value: 'id'
                },
                {
                    text: 'Distance (miles) From You',
                    value: 'distance'
                },
                {
                    text: 'Latitude',
                    value: 'lat'
                },
                {
                    text: 'Longitude',
                    value: 'lng'
                }
            ],
            buses: [
                {
                    id: 'some_id',
                    distance: '0',
                    lat: '40.785091',
                    lng: '-73.968285'
                }
            ]
        }
    },
    methods: {
        search: function() {
            if (this.query.length > 0) {
                axios
                    .get("/api/bus/" + this.query, {
                        params: {
                            location: this.mylocation.lat
                        }
                    })
                    .then(res => res.data)
                    .then(data => {
                        this.buses = data;
                        if (data.length > 0)
                            this.placeMarker(new google.maps.LatLng(data[0].lat, data[0].lng));
                    })
                    .catch(error => console.error("Couldn't load search:", error));
            }
        },
        selectRow: function(item, row) {
            this.placeMarker(new google.maps.LatLng(item.lat, item.lng));
            this.drawLine(this.mylocation, item);
        },
        placeMarker: function(location) {
            this.marker.setMap(null);
            this.marker = new google.maps.Marker({
                position: location,
                title: 'Selected bus',
                icon: 'http://maps.google.com/mapfiles/ms/micons/bus.png'
            });
            this.marker.setMap(this.map);
            this.map.setCenter(this.marker.getPosition());
        },
        drawLine: function(latlng0, latlng1) {
            if (this.path != null) this.path.setMap(null);
            let line = [
                {lat: latlng0.lat, lng: latlng0.lng},
                {lat: latlng1.lat, lng: latlng1.lng},
            ];
            this.path = new google.maps.Polyline({
                path: line,
                geodesic: true,
                strokeColor: '#000000'
            });
            this.path.setMap(this.map);
        },
        handleLocationError: function (browserHasGeolocation, infoWindow, pos) {
            this.infoWindow.setPosition(pos);
            this.infoWindow.setContent(
                browserHasGeolocation
                    ? "You must authorize your current location"
                    : "Error: Your browser doesn't support geolocation."
            );
            this.infoWindow.open(this.map);
        }
    },
    watch: {
        buses: function(val) {
        }
    },
    mounted: function() {
        this.map = new google.maps.Map(document.getElementById('b-map'), {
            center: { lat: -34.397, lng: 150.644 },
            scrollwheel: true,
            zoom: 15
        });
        this.infoWindow = new google.maps.InfoWindow();

        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    const pos = {
                        lat: position.coords.latitude,
                        lng: position.coords.longitude,
                    };
                    this.mylocation = {
                        lat: position.coords.latitude,
                        lng: position.coords.longitude
                    };
                    this.infoWindow.setPosition(pos);
                    this.infoWindow.setContent("You are here.");
                    this.infoWindow.open(this.map);
                    this.map.setCenter(pos);
                },
                () => {
                    this.handleLocationError(true, this.infoWindow, this.map.getCenter());
                }
            );
        } else {
            this.handleLocationError(false, this.infoWindow, this.map.getCenter());
        }

        this.marker = new google.maps.Marker({
            position: {lat: -25.363, lng: 131.044},
            map: this.map
        });
    }
}

</script>

<style>
#b-map {
    height:60vh ;
    width: 100%;
}
.theme--light.v-application {
    background-color: rgba(175, 175, 175, 0.12);
}
</style>
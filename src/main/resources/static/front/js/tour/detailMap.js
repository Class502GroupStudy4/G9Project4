window.addEventListener('DOMContentLoaded', () => {
    const mapEl = document.getElementById("map");
    const {lat, lng, address} = mapEl.dataset;

    const options = {
        center: {lat, lng},
        marker: {lat, lng},
        zoom: 4
    };
    if (options.center.lat !== undefined) {
        console.log("center");
        mapLib.load("map", 900, 600, options);
    } else if (address !== undefined) {
        try{
            mapLib.loadByAddress(address, 0, "map", 900, 600, options);
        } catch (e){
            mapLib.loadByAddress(address.split(" ",-1),"map",900,600,options);
        }
    }

});
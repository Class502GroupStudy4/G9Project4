window.addEventListener("DOMContentLoaded", function () {
    navigator.geolocation.getCurrentPosition(position => {
        const {latitude, longitude} = position.coords;
        console.log(longitude, latitude);
    })
})
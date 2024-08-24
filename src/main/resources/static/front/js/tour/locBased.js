window.addEventListener("DOMContentLoaded", function () {
        const location = document.getElementById("search-distance");
        location.addEventListener("click", function (e) {
            if (navigator.geolocation) {
                // GeoLocation을 이용해서 접속 위치를 얻어옵니다
                navigator.geolocation.getCurrentPosition(function (position) {

                    const currentLocation = {
                        latitude: position.coords.latitude, // 위도
                        longitude: position.coords.longitude,// 경도
                        radius: 1000
                    }
                    const queryString = new URLSearchParams(currentLocation).toString();
                    console.log(queryString);
                    fetch(`/tour/distance/list?${queryString}`, {
                        method: 'GET',
                    })
                        .then(response => {
                            if (!response.ok) {
                                throw new Error;
                            }
                            console.log(response.text());
                        })
                        .then(data => {
                            // 페이지 이동
                            // 예를 들어, '/tour/details'로 이동
                            window.location.href = `/tour/distance/list?${queryString}`;  // 원하는 URL로 변경 가능
                        }).catch(error => {
                        console.log(error);
                    })
                });
            }
        })
    }
)
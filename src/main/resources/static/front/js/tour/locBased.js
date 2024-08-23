window.addEventListener("DOMContentLoaded", function () {
        const location = document.getElementById("search-distance");
        location.addEventListener("click", function (e) {
            if (navigator.geolocation) {
                // GeoLocation을 이용해서 접속 위치를 얻어옵니다
                navigator.geolocation.getCurrentPosition(function (position) {

                    var lat = position.coords.latitude, // 위도
                        lon = position.coords.longitude; // 경도
                    console.log(lat,lon);
                    const url = `https://apis.data.go.kr/B551011/KorService1/locationBasedList1?MobileOS=and&MobileApp=test&_type=json&mapX=${lon}&mapY=${lat}&radius=1000&serviceKey=n5fRXDesflWpLyBNdcngUqy1VluCJc1uhJ0dNo4sNZJ3lkkaYkkzSSY9SMoZbZmY7%2FO8PURKNOFmsHrqUp2glA%3D%3D`;
                    const options = {
                        method : "GET",
                    }
                    fetch(url,options).then(function (response) {
                        console.log(response.json());
                    })
                });
            }
        })
    }
)
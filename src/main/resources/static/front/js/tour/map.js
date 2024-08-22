window.addEventListener("DOMContentLoaded", function() {
    var placeOverlay2 = new kakao.maps.CustomOverlay({zIndex:1}),
        contentNode2 = document.createElement('div'), // 커스텀 오버레이의 컨텐츠 엘리먼트 입니다
        markers2 = [], // 마커를 담을 배열입니다
        currCategory2 = ''; // 현재 선택된 카테고리를 가지고 있을 변수입니다

    var mapContainer2 = document.getElementById('map'), // 지도를 표시할 div
        mapOption2 = {
            center: new kakao.maps.LatLng(37.566826, 126.9786567), // 지도의 중심좌표
            level: 3 // 지도의 확대 레벨
        };

// 지도를 생성합니다
    var map2 = new kakao.maps.Map(mapContainer2, mapOption2);

// 장소 검색 객체를 생성합니다
    var ps2 = new kakao.maps.services.Places(map2);

// 지도에 idle 이벤트를 등록합니다
    kakao.maps.event.addListener(map2, 'idle', searchPlaces2);

// 커스텀 오버레이의 컨텐츠 노드에 css class를 추가합니다
    contentNode2.className = 'placeinfo_wrap';

// 커스텀 오버레이의 컨텐츠 노드에 mousedown, touchstart 이벤트가 발생했을때
// 지도 객체에 이벤트가 전달되지 않도록 이벤트 핸들러로 kakao.maps.event.preventMap 메소드를 등록합니다
    addEventHandle2(contentNode2, 'mousedown', kakao.maps.event.preventMap);
    addEventHandle2(contentNode2, 'touchstart', kakao.maps.event.preventMap);

// 커스텀 오버레이 컨텐츠를 설정합니다
    placeOverlay2.setContent(contentNode2);

// 각 카테고리에 클릭 이벤트를 등록합니다
    addCategoryClickEvent2();

// 엘리먼트에 이벤트 핸들러를 등록하는 함수입니다
    function addEventHandle2(target, type, callback) {
        if (target.addEventListener) {
            target.addEventListener(type, callback);
        } else {
            target.attachEvent('on' + type, callback);
        }
    }

// 카테고리 검색을 요청하는 함수입니다
    function searchPlaces2() {
        if (!currCategory2) {
            return;
        }

        // 커스텀 오버레이를 숨깁니다
        placeOverlay2.setMap(null);

        // 지도에 표시되고 있는 마커를 제거합니다
        removeMarker2();

        ps2.categorySearch(currCategory2, placesSearchCB2, {useMapBounds:true});
    }

// 장소검색이 완료됐을 때 호출되는 콜백함수 입니다
    function placesSearchCB2(data, status, pagination) {
        if (status === kakao.maps.services.Status.OK) {

            // 정상적으로 검색이 완료됐으면 지도에 마커를 표출합니다
            displayPlaces2(data);
        } else if (status === kakao.maps.services.Status.ZERO_RESULT) {
            // 검색결과가 없는경우 해야할 처리가 있다면 이곳에 작성해 주세요

        } else if (status === kakao.maps.services.Status.ERROR) {
            // 에러로 인해 검색결과가 나오지 않은 경우 해야할 처리가 있다면 이곳에 작성해 주세요

        }
    }

// 지도에 마커를 표출하는 함수입니다
    function displayPlaces2(places) {

        // 몇번째 카테고리가 선택되어 있는지 얻어옵니다
        // 이 순서는 스프라이트 이미지에서의 위치를 계산하는데 사용됩니다
        var order2 = document.getElementById(currCategory2).getAttribute('data-order');

        for ( var i=0; i<places.length; i++ ) {

            // 마커를 생성하고 지도에 표시합니다
            var marker2 = addMarker2(new kakao.maps.LatLng(places[i].y, places[i].x), order2);

            // 마커와 검색결과 항목을 클릭 했을 때
            // 장소정보를 표출하도록 클릭 이벤트를 등록합니다
            (function(marker2, place) {
                kakao.maps.event.addListener(marker2, 'click', function() {
                    displayPlaceInfo2(place);
                });
            })(marker2, places[i]);
        }
    }

// 마커를 생성하고 지도 위에 마커를 표시하는 함수입니다
    function addMarker2(position, order) {
        var imageSrc2 = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/places_category.png', // 마커 이미지 url, 스프라이트 이미지를 씁니다
            imageSize2 = new kakao.maps.Size(27, 28),  // 마커 이미지의 크기
            imgOptions2 =  {
                spriteSize : new kakao.maps.Size(72, 208), // 스프라이트 이미지의 크기
                spriteOrigin : new kakao.maps.Point(46, (order*36)), // 스프라이트 이미지 중 사용할 영역의 좌상단 좌표
                offset: new kakao.maps.Point(11, 28) // 마커 좌표에 일치시킬 이미지 내에서의 좌표
            },
            markerImage2 = new kakao.maps.MarkerImage(imageSrc2, imageSize2, imgOptions2),
            marker2 = new kakao.maps.Marker({
                position: position, // 마커의 위치
                image: markerImage2
            });

        marker2.setMap(map2); // 지도 위에 마커를 표출합니다
        markers2.push(marker2);  // 배열에 생성된 마커를 추가합니다

        return marker2;
    }

// 지도 위에 표시되고 있는 마커를 모두 제거합니다
    function removeMarker2() {
        for ( var i = 0; i < markers2.length; i++ ) {
            markers2[i].setMap(null);
        }
        markers2 = [];
    }

// 클릭한 마커에 대한 장소 상세정보를 커스텀 오버레이로 표시하는 함수입니다
    function displayPlaceInfo2 (place) {
        var content2 = '<div class="placeinfo">' +
            '   <a class="title" href="' + place.place_url + '" target="_blank" title="' + place.place_name + '">' + place.place_name + '</a>';

        if (place.road_address_name) {
            content2 += '    <span title="' + place.road_address_name + '">' + place.road_address_name + '</span>' +
                '  <span class="jibun" title="' + place.address_name + '">(지번 : ' + place.address_name + ')</span>';
        }  else {
            content2 += '    <span title="' + place.address_name + '">' + place.address_name + '</span>';
        }

        content2 += '    <span class="tel">' + place.phone + '</span>' +
            '</div>' +
            '<div class="after"></div>';

        contentNode2.innerHTML = content2;
        placeOverlay2.setPosition(new kakao.maps.LatLng(place.y, place.x));
        placeOverlay2.setMap(map2);
    }

// 각 카테고리에 클릭 이벤트를 등록합니다
    function addCategoryClickEvent2() {
        var category2 = document.getElementById('category'),
            children2 = category2.children;

        for (var i=0; i<children2.length; i++) {
            children2[i].onclick = onClickCategory2;
        }
    }

// 카테고리를 클릭했을 때 호출되는 함수입니다
    function onClickCategory2() {
        var id2 = this.id,
            className2 = this.className;

        placeOverlay2.setMap(null);

        if (className2 === 'on') {
            currCategory2 = '';
            changeCategoryClass2();
            removeMarker2();
        } else {
            currCategory2 = id2;
            changeCategoryClass2(this);
            searchPlaces2();
        }
    }

// 클릭된 카테고리에만 클릭된 스타일을 적용하는 함수입니다
    function changeCategoryClass2(el) {
        var category2 = document.getElementById('category'),
            children2 = category2.children,
            i;

        for ( i=0; i<children2.length; i++ ) {
            children2[i].className = '';
        }

        if (el) {
            el.className = 'on';
        }
    }


    function handlePlaceClick(element) {
        // data-lat와 data-lng 속성에서 좌표를 가져옵니다
        var lat = parseFloat(element.getAttribute('data-lat'));
        var lng = parseFloat(element.getAttribute('data-lng'));

        // 새로운 마커의 위치를 설정합니다
        var markerPosition = new kakao.maps.LatLng(lat, lng);

        // 기존 마커를 지웁니다 (만약 한 개의 마커만 표시하려고 한다면)
        removeMarker2();

        // 새로운 마커를 추가합니다
        var marker = addMarker2(markerPosition);

        // 지도 중심을 클릭한 장소로 이동시킵니다
        map2.setCenter(markerPosition);

        // 마커를 배열에 저장합니다
        markers2.push(marker);
    }

// 마커를 추가하는 함수 (기존의 addMarker2 함수 사용)
    function addMarker2(position) {
        var marker = new kakao.maps.Marker({
            position: position
        });

        marker.setMap(map2); // 지도에 마커를 표시합니다
        return marker;
    }

// 기존의 removeMarker2 함수
    function removeMarker2() {
        for (var i = 0; i < markers2.length; i++) {
            markers2[i].setMap(null);
        }
        markers2 = [];
    }
});

/*메인페이지 상단 바탕이미지 시작 */
document.addEventListener("DOMContentLoaded", function() {
    const images = [
        'front/images/background1.jpg',
        'front/images/background2.jpg'
    ];
    let currentIndex = 0;

    function changeBackgroundImage() {
        currentIndex = (currentIndex + 1) % images.length;
        document.body.style.backgroundImage = `url('${images[currentIndex]}')`;
    }

    // 처음 배경 이미지 설정
    document.body.style.backgroundImage = `url('${images[currentIndex]}')`;

    // 5초마다 배경 이미지 변경
    setInterval(changeBackgroundImage, 5000);
});

/*메인페이지 상단 바탕이미지 끝 */
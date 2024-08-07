/*메인페이지 상단 바탕이미지 시작 */

document.addEventListener("DOMContentLoaded", function() {
    const mainEl = document.querySelector("main");
    setInterval(function() {
        mainEl.classList.toggle("change");
    }, 5000);
});
/*메인페이지 상단 바탕이미지 끝 */
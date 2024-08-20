function showTab(tabId) {
    // 모든 탭과 콘텐츠 비활성화
    document.querySelectorAll('.tab, .tab-content').forEach(el => el.classList.remove('active'));

    // 선택된 탭과 콘텐츠 활성화
    document.getElementById('tab-' + tabId).classList.add('active');
    document.getElementById('content-' + tabId).classList.add('active');
}

// 페이지 로드 시 기본 탭을 활성화
document.addEventListener('DOMContentLoaded', function () {
    const defaultTab = document.querySelector('.tab.active');
    if (defaultTab) {
        showTab(defaultTab.id.replace('tab-', ''));
    }
});
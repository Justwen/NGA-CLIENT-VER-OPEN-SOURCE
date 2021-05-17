function toggleCollapse(button, text) {
    var collapse = button.parentElement.getElementsByTagName("div")[0];

    if (collapse.style.display == 'none') {
        collapse.style.display = '';
        button.innerHTML = button.innerHTML.replace("显示", "隐藏");
    } else {
        collapse.style.display = 'none';
        button.innerHTML = button.innerHTML.replace("隐藏", "显示");
    }
}

function modifyEmotionSize() {
/*    var acEmotions = document.getElementsByClassName("emoticon")
    if (acEmotions != null && acEmotions.length > 0) {
        var size = window.action.getEmotionSize();
        for (let i = 0; i < acEmotions.length; i++) {
            acEmotions[i].style.width = size;
        }
    }*/
}
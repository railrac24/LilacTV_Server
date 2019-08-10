$(".answer-write input[type=submit]").click(addAnswer);

function addAnswer(e) {
    e.preventDefault();
    var queryString = $('.answer-write').serialize();
    var url = $(".answer-write").attr("action");

    if (queryString !== "content=") {
        $.ajax({
                type: 'post',
                url: url,
                data: queryString,
                dataType: 'json',
                error: onError,
                success: onSuccess
            }
        );
    } else alert("답글 내용이 없습니다.");
}

function refreshCount(count) {
    var countOfAnswerTemplate = $("#countOfAnswerTemplate").html();
    var template2 = countOfAnswerTemplate.format(count);
    $(".qna-comment-count").remove();
    $(".qna-comment-slipp").prepend(template2);
}

function onError() {
    alert("로그인 후 사용하세요.");
}

function onSuccess(data, status) {
    var answerTemplate = $("#answerTemplate").html();
    var template1 = answerTemplate.format(data.replier.name, data.formattedCreateDate, data.content, data.questions.id, data.id);
    $(".qna-comment-slipp-articles").prepend(template1);

    $(".answer-write textarea").val('');
    refreshCount(data.questions.countOfAnswers);
}

$(".qna-comment-slipp-articles").on("click","a.link-delete-article", function(e){
    e.preventDefault();
    var deleteBtn = $(this);
    var url = deleteBtn.attr("href");

    $.ajax({
        type : 'delete',
        url : url,
        dataType :'json',
        error : function (xhr, status) {
            console.log("error");
        },
        success : function (data, status) {
            if (data.valid) {
                deleteBtn.closest("article").remove();
                refreshCount(data.count);
            } else alert(data.errorMessage);
        }
    });
});

String.prototype.format = function() {
    var args = arguments;
    return this.replace(/{(\d+)}/g, function(match, number) {
        return typeof args[number] != 'undefined'
            ? args[number]
            : match
            ;
    });
};
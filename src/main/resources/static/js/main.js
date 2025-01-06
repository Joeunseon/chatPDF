/**
 * main.js
 */

// Element detach 관리
const elements = {
    divPdfDetach : $('.divPdf').detach(), // pdf 페이지
    spanAiDetach : $('.spanAi').detach(), // 첫 요약 및 예시
    divHDetach : $('.divH').detach(),     // 질문
    divADetach : $('.divA').detach(),     // 답변
    divLDetach : $('.divL').detach()      // chat loading
};

let maxRequestSize; // 서버로 보낼수있는 파일 최대 크기
let maxFileSize;	// 단일 파일 최대 크기

$(document).ready(function () {

    fileInit();

    // 파일 업로드시
    $('#inputFile').bind('change', function() {
        uploadFile(this.files);
    });

    // New Chat 클릭시
    $('.ant-upload-btn').on('click', function () {
        let fileInput = document.getElementById('inputFile');
        fileInput.click();
    });

    // 질문 엔터시
    $('.ant-input').keypress(function(e) {
        if (e.which == 13) {
          e.preventDefault();
          askAi();
        }
    });
});

/**
 * 파일 init
 * properties에 설정된 maxRequestSize, maxFileSize 취득
 */
function fileInit() {
    const url = '/api/file/multipart';

    fn_fetchGetData(url)
        .then(data => {
            maxRequestSize = data.maxRequestSize;
            maxFileSize = maxFileSize;
        })
        .catch(fn_handleError);
}

// 파일 업로드시
function uploadFile(fileObject) {
    let files = fileObject;

    if ( fileObject != null ) {
        // 파일 이름 
        let fileName = fileObject[0].name;
        let fileNameArr = fileName.split("\.");
        // 확장자
        let ext = fileNameArr[fileNameArr.length - 1];

        if ($.inArray(ext, [ 'pdf' ]) == -1) {
            alert("pdf 파일만 업로드 가능합니다. ("+fileName+")");
            return;
        }

        let fileSize = fileObject[0].size;
        if ( fileSize <= 0 ) {
            alert('파일크기가 0kb 입니다.');
            return;
        }

        if ( fileSize > maxFileSize ) {
            alert('업로드 가능한 파일의 최대 크기는 ' + maxFileSize + ' 입니다.');
            return;
        }
    
        // 파일 뷰어 제목 설정
        $('.divMiddle').find('h1').text(fileName);
        // 파일 뷰어 페이지 초기화
        $('#pdfContainer').empty();
        // 전체 화면 로딩 
        $('#loadingDiv').css('display', 'flex');
        // 파일 뷰어
        renderPDF(fileObject[0]);
        // 파일 업로드
        fileUpload();
    } else {
        alert("ERROR");
    }
}

// 파일 업로드
function fileUpload() {
    // id 초기화
	$('#sourceId').val('');

    let formData = new FormData();
    let fileInput = document.querySelector('input[type="file"]');
    formData.append('file', fileInput.files[0]);
    let flag = false;

    const url = '/api/file';

    fn_fetchPostData(url, formData)
        .then(data => {
            let dataMap = data.dataMap;
            if ( data.code == '200' ) {
                $('#sourceid').val(dataMap.sourceId);
                flag = true;
            } else {
                alert(dataMap.message);
            }
        })
        .catch(fn_handleError);

    if ( flag ) {
        // 요약과 질문 예시
        firstAskAi();
    } else {
        $('#loadingDiv').hide();
    }
}
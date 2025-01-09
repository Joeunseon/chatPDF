/**
 * Common js
 */

// get Fetch 요청 헤더
const headers = {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
};

/**
 * 공통 Fetch GET 요청 유틸리티
 * @param {string} url - 요청 URL
 * @returns {Promise} - Fetch 결과 Promise
 */
function fn_fetchGetData(url) {
    return fetch(url, headers).then(response => {
        if (!response.ok) throw new Error('Failed to fetch data');
        return response.json();
    });
}

// Fetch GET blob 요청 유틸리티
function fn_fetchGetBlod(url) {
    return fetch(url, headers).then(response => {
        if (!response.ok) throw new Error('Failed to fetch data');
        return response.blob();
    });
}

/**
 * 공통 Fetch POST 요청 유틸리티
 * @param {string} url - 요청 URL
 * @param {Oject} formData - 요청 data
 * @returns {Promise} - Fetch 결과 Promise
 */
function fn_fetchPostData(url, formData) {
    return fetch(url, {
        method: 'POST',
        body: formData
    }).then(response => {
        if (!response.ok) throw new Error('Failed to fetch data');
        return response.json();
    });
}

function fn_fetchPostData(url, formData, header) {
    return fetch(url, {
        method: 'POST',
        headers: header,
        body: formData
    }).then(response => {
        if (!response.ok) throw new Error('Failed to fetch data');
        return response.json();
    });
}

function fn_fetchDeleteData(url) {
    return fetch(url, {
        method: 'DELETE',
        headers: {
        'Content-Type': 'application/json',
        }
    }).then(response => {
        if (!response.ok) throw new Error('Failed to fetch data');
        return response.json();
    });
}

/**
 * 에러 처리
 * @param {Error} err - 에러 객체
 */
function fn_handleError(err) {
    console.error('Error occurred:', err.message);
    alert('오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
}
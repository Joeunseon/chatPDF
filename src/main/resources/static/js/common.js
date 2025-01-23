/**
 * Common.js - 공통 Fetch 유틸리티
 */

/**
 * Fetch 요청 공통 처리
 * @param {string} url - 요청 URL
 * @param {Object} options - Fetch 옵션
 * @returns {Promise} - Fetch 결과 Promise
 */
function fn_fetchRequest(url, options = {}) {
    const defaultHeaders = {
        'Content-Type': 'application/json',
    };

    // FormData 사용 시 Content-Type 제거
    if (options.body instanceof FormData) {
        delete defaultHeaders['Content-Type'];
    }

    // 옵션에 헤더 병합
    options.headers = { ...defaultHeaders, ...options.headers };

    return fetch(url, options)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Fetch failed: ${response.status} ${response.statusText}`);
            }
            return response;
        })
        .catch(fn_handleError);
}

/**
 * GET 요청
 * @param {string} url - 요청 URL
 * @returns {Promise} - JSON 데이터를 반환
 */
function fn_fetchGetData(url) {
    return fn_fetchRequest(url, { method: 'GET' }).then(response => response.json());
}

/**
 * GET Blob 요청
 * @param {string} url - 요청 URL
 * @returns {Promise} - Blob 데이터를 반환
 */
function fn_fetchGetBlob(url) {
    return fn_fetchRequest(url, { method: 'GET' }).then(response => response.blob());
}

/**
 * POST 요청
 * @param {string} url - 요청 URL
 * @param {Object} body - 요청 데이터
 * @param {Object} headers - 추가 헤더 옵션
 * @returns {Promise} - JSON 데이터를 반환
 */
function fn_fetchPostData(url, body, headers = {}) {
    return fn_fetchRequest(url, {
        method: 'POST',
        headers,
        body: body instanceof FormData ? body : JSON.stringify(body),
    }).then(response => response.json());
}

/**
 * PATCH 요청
 * @param {string} url - 요청 URL
 * @param {Object} body - 요청 데이터
 * @param {Object} headers - 추가 헤더 옵션
 * @returns {Promise} - JSON 데이터를 반환
 */
function fn_fetchPatchData(url, body, headers = {}) {
    return fn_fetchRequest(url, {
        method: 'PATCH',
        headers,
        body: JSON.stringify(body),
    }).then(response => response.json());
}

/**
 * DELETE 요청
 * @param {string} url - 요청 URL
 * @returns {Promise} - JSON 데이터를 반환
 */
function fn_fetchDeleteData(url) {
    return fn_fetchRequest(url, { method: 'DELETE' }).then(response => response.json());
}

/**
 * 에러 처리
 * @param {Error} err - 에러 객체
 */
function fn_handleError(err) {
    console.error('Error occurred:', err.message);
    alert('오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
    throw err; // 재처리를 위해 에러를 다시 던짐
}
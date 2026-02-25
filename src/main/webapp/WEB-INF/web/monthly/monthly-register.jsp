<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>월정액 회원 등록 - 스마트주차 반월당점</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/public.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<div class="container">

    <!-- ========== 헤더 ========== -->
    <header class="dashboard-header">
        <%@include file="../common/header.jsp" %>
    </header>

    <!-- ========== 오류 메시지 ========== -->
    <%-- register는 redirect로 오기 때문에 param으로 읽음 --%>
    <%
        /* redirect 후 URL 파라미터로 전달된 에러 코드를 읽어 메시지로 변환 */
        String error = request.getParameter("error");
        if (error != null && !error.isEmpty()) {
            String errorMsg;
            switch (error) {
                case "duplicate":     errorMsg = "이미 등록된 기간과 겹칩니다. 날짜를 확인해 주세요."; break;
                case "empty":         errorMsg = "모든 항목을 입력해 주세요.";                        break;
                case "invaliddate":   errorMsg = "날짜 형식이 올바르지 않습니다.";                    break;
                case "invalidmonths": errorMsg = "개월 수는 1~12 사이로 입력해 주세요.";              break;
                default:              errorMsg = "입력 오류가 발생했습니다. 다시 시도해 주세요.";      break;
            }
    %>
    <div class="alert alert-error">
        <i class="fa-solid fa-circle-exclamation"></i>
        <%= errorMsg %>
    </div>
    <% } %>

    <!-- ========== 등록 폼 카드 ========== -->
    <div class="policy-form-card">

        <!-- 카드 제목 -->
        <div class="card-title">
            <i class="fas fa-user-plus"></i> 월정액 회원 신규 등록
        </div>

        <form action="${pageContext.request.contextPath}/monthly/register" method="post">

            <div class="form-row">

                <!-- 차량번호: blur 시 기존 회원 정보 자동 조회 -->
                <div class="form-group">
                    <label for="plateNumber"><i class="fas fa-car"></i> 차량번호 <span style="color: var(--danger-color);">*</span></label>
                    <input type="text"
                           id="plateNumber"
                           name="plateNumber"
                           placeholder="예) 123가4567"
                           maxlength="20"
                           required>
                    <%-- 기존 회원 조회 결과 표시 (연장 시 이름/연락처 자동 입력, 다음 시작일 안내) --%>
                    <p class="help-text" id="memberHint" style="color: var(--primary-color);"></p>
                </div>

                <!-- 이름 -->
                <div class="form-group">
                    <label for="name"><i class="fas fa-user"></i> 이름 <span style="color: var(--danger-color);">*</span></label>
                    <input type="text"
                           id="name"
                           name="name"
                           placeholder="회원 이름"
                           maxlength="20"
                           required>
                </div>

            </div>

            <div class="form-row">

                <!-- 연락처 -->
                <div class="form-group">
                    <label for="phoneNumber"><i class="fas fa-phone"></i> 연락처 <span style="color: var(--danger-color);">*</span></label>
                    <input type="text"
                           id="phoneNumber"
                           name="phoneNumber"
                           placeholder="예) 010-1234-5678"
                           maxlength="13"
                           required>
                    <p class="help-text">형식: 010-0000-0000 (최대 13자)</p>
                </div>

            </div>

            <div class="form-row">

                <!-- 시작일: 연장 회원이면 AJAX로 자동 세팅 -->
                <div class="form-group">
                    <label for="beginDate"><i class="fas fa-calendar-plus"></i> 시작일 <span style="color: var(--danger-color);">*</span></label>
                    <input type="date"
                           id="beginDate"
                           name="beginDate"
                           required>
                </div>

                <!-- 개월 수: 기존 만료일 직접 입력 방식 → 개월 수 입력으로 변경 -->
                <%-- Service에서 개월 수만큼 행 분리 후 INSERT --%>
                <%-- ex) 3개월 → 3행: 1개월씩 begin/expiry 계산 후 저장 --%>
                <div class="form-group">
                    <label for="months"><i class="fas fa-calendar-alt"></i> 등록 개월 수 <span style="color: var(--danger-color);">*</span></label>
                    <input type="number"
                           id="months"
                           name="months"
                           min="1"
                           max="12"
                           value="1"
                           required>
                    <p class="help-text" id="expiryPreview">만료 예정일이 여기에 표시됩니다.</p>
                </div>

            </div>

            <!-- 버튼 그룹 -->
            <%-- [수정] btn-primary + btn-success 중복 클래스 → btn-primary 단독 사용으로 변경 --%>
            <%-- 원인: CSS에서 btn-success가 btn-primary 이후 선언되어 background를 덮어쓰고 있었음 --%>
            <div class="btn-group">
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i> 등록
                </button>
                <a href="${pageContext.request.contextPath}/monthly/list" style="flex: 1;">
                    <button type="button" class="btn btn-secondary" style="width: 100%;">
                        <i class="fas fa-arrow-left"></i> 목록으로
                    </button>
                </a>
            </div>

        </form>
    </div>

</div>

<script>
    const contextPath = "${pageContext.request.contextPath}";

    // 차량번호 blur 시 기존 회원 정보 조회 (연장 여부 확인)
    document.getElementById('plateNumber').addEventListener('blur', function () {
        const plateNumber = this.value.trim();
        if (!plateNumber) return;

        fetch(contextPath + '/monthly/getNextBeginDate?plateNumber=' + encodeURIComponent(plateNumber))
            .then(function(res) { return res.json(); })
            .then(function(data) {
                // 시작일 자동 세팅
                document.getElementById('beginDate').value = data.nextBeginDate;

                // 기존 회원이면 이름/연락처 자동 입력 + 안내 문구 표시
                if (data.name) {
                    document.getElementById('name').value        = data.name;
                    document.getElementById('phoneNumber').value = data.phoneNumber;
                    document.getElementById('memberHint').textContent =
                        '기존 회원 정보를 불러왔습니다. 시작일: ' + data.nextBeginDate;
                } else {
                    document.getElementById('memberHint').textContent =
                        '신규 회원입니다.';
                }

                // 만료 예정일 미리보기 갱신
                updateExpiryPreview();
            })
            .catch(function() {
                // 조회 실패 시 오늘 날짜로 초기화
                const today = new Date().toISOString().split('T')[0];
                document.getElementById('beginDate').value = today;
            });
    });

    // 시작일 또는 개월 수 변경 시 만료 예정일 미리보기 갱신
    document.getElementById('beginDate').addEventListener('change', updateExpiryPreview);
    document.getElementById('months').addEventListener('input',  updateExpiryPreview);

    function updateExpiryPreview() {
        const beginVal = document.getElementById('beginDate').value;
        const months   = parseInt(document.getElementById('months').value);

        if (!beginVal || isNaN(months) || months < 1) {
            document.getElementById('expiryPreview').textContent = '만료 예정일이 여기에 표시됩니다.';
            return;
        }

        // 익월 같은 날 -1일 계산 (Service의 calcExpiryDate와 동일 로직)
        const begin  = new Date(beginVal);
        const expiry = new Date(begin);
        expiry.setMonth(expiry.getMonth() + months);
        expiry.setDate(expiry.getDate() - 1);

        const expiryStr = expiry.toISOString().split('T')[0];
        document.getElementById('expiryPreview').textContent =
            months + '개월 등록 → 만료 예정일: ' + expiryStr;
    }

    // 페이지 로드 시 오늘 날짜를 시작일 기본값으로
    window.addEventListener('DOMContentLoaded', function () {
        const today = new Date().toISOString().split('T')[0];
        const beginDateInput = document.getElementById('beginDate');
        if (!beginDateInput.value) {
            beginDateInput.value = today;
            updateExpiryPreview();
        }
    });
</script>

</body>
</html>
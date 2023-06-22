/**
	2. 인기페이지
	(1) 페이지 로드하기
	(2) 인기페이지 스크롤 페이징하기
 */

//	(0) 현재 로그인한 사용자 아이디
let principalId = $("#principalId").val();

// (1) 페이지 로드하기

let page = 0;

function popularLoad() {
  $.ajax({
    url: `/api/image/popular?page=${page}`,
    dataType: "json",
  })
    .done((res) => {
//      console.log(res);
      res.data.content.forEach((image) => {
        let popularItem = getPopularItem(image);
        $("#popularList").append(popularItem);
      });
    })
    .fail((err) => {
      console.log("오류: ", err);
    });
}

popularLoad();

function getPopularItem(image) {
  let item = `
              <div class="p-img-box">
                  <a href="/user/${image.user.id}"> <img src="/upload/${image.postImageUrl}" />
                  </a>
              </div>
              `
  return item;
}

// (2) 인기페이지 스크롤 페이징하기
$(window).scroll(() => {

  let checkNum = $(window).scrollTop() - ($(document).height() - $(window).height());

  if (checkNum < 1 && checkNum > -1) {
    page++;
    popularLoad();
  }
});

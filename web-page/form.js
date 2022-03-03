$(document).ready(function () {
  $("form").submit(function (event) {

    var request = $.ajax({
      type: "POST",
      url: "https://classic-den-add-entry-function.azurewebsites.net/api/entry",
      dataType: "json",
      contentType: "application/json",
      data: JSON.stringify({
        title: $("#titleText").val(),
        author: $("#authorText").val(),
        message: $("#descriptionTextarea").val()
      }),
      statusCode: {
        202: function() {
            $('#addEntryModal').modal('hide');
            document.getElementById("form").reset();
        }
      }
    });

    request.done(function (data) {
        console.log(data);
        console.log('Sth');
        if (!data.success) {} else {}

    });


    event.preventDefault();
  });
});
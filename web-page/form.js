$(document).ready(function () {
  $("form").submit(function (event) {
    
    $('#addEntryModal').modal('hide');
    document.getElementById('alert-info').hidden = false;
    
    var request = $.ajax({
      type: "POST",
      url: "https://classic-den-functions.azurewebsites.net/api/entry",
      dataType: "json",
      contentType: "application/json",
      data: JSON.stringify({
        title: $("#titleText").val(),
        author: $("#authorText").val(),
        message: $("#descriptionTextarea").val()
      }),
      statusCode: {
        202: function() {
            document.getElementById('alert-info').hidden = true;
            document.getElementById('alert-success').hidden = false;
            document.getElementById("form").reset();
        }
      },
      error: function(xhr, ajaxOptions, thrownError) {
        if(xhr.status != 202){
          document.getElementById('alert-info').hidden = true;
          document.getElementById('alert-error').hidden = false;
        }
      }
    });

   event.preventDefault();
  });

  $("#alert-info-btn").click(function() {
    document.getElementById('alert-info').hidden = true;
  });

  $("#alert-success-btn").click(function() {
    document.getElementById('alert-success').hidden = true;
  });

  $("#alert-error-btn").click(function() {
    document.getElementById('alert-error').hidden = true;
  });
});
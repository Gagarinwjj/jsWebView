 function DateSelect(eleId) {
            var dateChoose = document.getElementById(eleId);
            //alert(dateChoose.value);
            //alert(dateChoose.id);
            javascript:window.myjs.chooseDate(dateChoose.value,dateChoose.id);
        }
        
  function setDate(dateStr,eleId){
    var dateChoose = document.getElementById(eleId);
   // alert(dateStr);
   // alert(eleId);
    dateChoose.setAttribute('value',dateStr);
  }
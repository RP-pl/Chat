  function accept(event){
  for(const child of event.target.parentNode.parentNode.parentNode.parentNode.childNodes){
    if(child.nodeName == "DIV"){
      if(child.innerHTML.slice(1,3) == "h4"){
        for(const element of child.childNodes){
          if(element.nodeName == "H4"){
            fetch("/accept",{
               method: "GET",
               mode: 'cors',
               headers:{
                 "username" : element.innerHTML,
              }
            })
            event.target.parentNode.parentNode.parentNode.parentNode.setAttribute("style","display:none")
          }
        }
      }
    }
  }
  }
  function decline(event){
  for(const child of event.target.parentNode.parentNode.parentNode.parentNode.childNodes){
    if(child.nodeName == "DIV"){
      if(child.innerHTML.slice(1,3) == "h4"){
        for(const element of child.childNodes){
          if(element.nodeName == "H4"){
            fetch("/decline",{
               method: "GET",
               mode: 'cors',
               headers:{
                 "username" : element.innerHTML,
              }
            })
            event.target.parentNode.parentNode.parentNode.parentNode.setAttribute("style","display:none")
          }
        }
      }
    }
  }
  }
export {accept,decline};
var ctx = demo.getContext('2d'),
            w = demo.width,
            h = demo.height,
            px = 0, opx = 0, speed = 5,
            py = h * .5, opy = py,
            scanBarWidth = 20,
                bpmList=[57,60,61,60,61,61,59,58,57,57,58,60],
                hrList=[3,4,5,6,7.1,6,5,4,3,4,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,6,7,6,4,2,5,5,5,6,6,6,6,7,7.1,7,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5],
                beats=0;
        // for(i=0;i<10;i++){
                // for(j=3;j<8;j++){
                        // hrList[hrList.length]=j*h*.1;// .5*h = middle
                // }
        // }
        ctx.strokeStyle = '#00bd00';
        ctx.lineWidth = 3;
       
        loop();
        var i=0,j=0;
        function loop() {
                if(j==bpmList.length){
                                j=0;
                }
                var hr = document.getElementById('hr');
                hr.innerHTML=bpmList[j];
               
               
                if(i==hrList.length){
                                i=0;
                }
                py=hrList[i]*h*.1;
                px += speed;
           
            ctx.clearRect(px,0, scanBarWidth, h);
            ctx.beginPath();
               
            ctx.moveTo(opx, opy);
            ctx.lineTo(px, py);
            ctx.stroke();
           
            opx = px;
            opy = py;
           
            if (opx > w) {
                px = opx = -speed;
            }
                if(hrList[i]==7.1){
                        j++;
                }
            i++;
               
               
            requestAnimationFrame(loop);
        }
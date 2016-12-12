function [index]=LinearSearch_KLS(rr,R,e,istart,iend)
  %Perform a linear search to find the next process

  s=0.0;
  eR=e*R;
  found=0;
%  istart,iend
  for index = istart:iend
     s = s + rr(index);
     if ( s >= eR )
       found=1;
       break
     end
  end
  if ( found == 0 )
     fprintf(1,'error!!! did not find the next process!!! \n')  
     pause   
  end 

return

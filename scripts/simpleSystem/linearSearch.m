function [i,j,proc]=linearSearch(v,R,E,N1,N2)
%
% 
%

found=0;
ss=0;
for i=1:N1
    for j=1:N2
        for proc=1:8
            ss=ss+v(i,j,proc);
            if (ss>=E*R)
                found=1;
                break;
            end
        end
        if found==1, break, end
    end
    if found==1, break, end
end

if found == 0
   fprintf(1,'Error: Could not find next event during Linear Search! Aborting...')
   pause
end

return

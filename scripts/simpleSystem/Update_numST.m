function [numST,NMA,NFA]=Update_numST(n1,n2,numST,val,St)

if ( val ~= 1 && val ~= -1 ), fprintf(1,'Error: VAL takes a wrong value at Update_numST. Aborting...'), pause, end

if val == 1
    if St(n1,n2) <= 4
        numST( St(n1,n2)   ) = numST( St(n1,n2)   ) - 1;
        numST( St(n1,n2)+1 ) = numST( St(n1,n2)+1 ) + 1;
    end
elseif val == -1
    if St(n1,n2) <= 3
        numST( St(n1,n2)+1 ) = numST( St(n1,n2)+1 ) + 1;
        numST( St(n1,n2)+2 ) = numST( St(n1,n2)+2 ) - 1;
    end
end

% if val == 1
%     if (St(n1,n2)>1 && St(n1,n2)<= 4)
%         numST( St(n1,n2)-1 ) = numST( St(n1,n2)-1 ) - 1;
%         numST( St(n1,n2)   ) = numST( St(n1,n2)   ) + 1;
%     elseif (St(n1,n2)==1)
%         numST( St(n1,n2)   ) = numST( St(n1,n2)   ) + 1;
%     end
% elseif val == -1
%     if (St(n1,n2)>0 && St(n1,n2)<= 3)
%         numST( St(n1,n2)+1 ) = numST( St(n1,n2)+1 ) - 1;
%         numST( St(n1,n2)   ) = numST( St(n1,n2)   ) + 1;
%     elseif (St(n1,n2)==0)
%         numST( St(n1,n2)+1 ) = numST( St(n1,n2)+1 ) - 1;
%     end
% end






NMA = sum( numST(1:4) ); %Number of Mobile Atoms
NFA = numST(5); %Number of Frozen Atoms

return

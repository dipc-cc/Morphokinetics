function [i,n1,n2,proc]=perform_kls(k,g,rlevel,R,ee,N1,N2)

%Perform k-level search to find the next process:
  i = 1;
  for lev = 1:k
    istart = (i-1)*g+1;
    iend = i*g;
    %rtotal = sum(rlevel(lev).element(istart:iend));
    if ( lev == 1)
      rtotal = R;
    else
      rtotal = rlevel(lev-1).element(i);
      %rtotal = sum( rlevel(lev).element(istart:iend) );
      %rtotalApu = sum( rlevel(lev).element(istart:iend) );
      %if abs(1-rtotalApu/rtotal) > 1e-8, fprintf(1,'Stored partial sum is different from calculated...'), pause, end 
%         if sum( rlevel(lev).element(istart:iend) )==0
%             lev,istart,iend
%         end
    end
   % ee(lev)
   %  rtotal
    % sum( rlevel(lev).element )
    i = LinearSearch_KLS(rlevel(lev).element,rtotal,ee(lev),istart,iend);
 %   fprintf(1,'%8i / %8i %8i %8.4e %8i \n',lev,istart,iend,rtotal,i)
  end
  

%For index defined as: i = 8*N2*(n1-1) + 8*(n2-1) + proc, use:
n1 = ceil( i/N2/8 );
apu = i - (n1-1)*N2*8;
n2 = ceil( apu/8 );
proc = apu - (n2-1)*8;

% For index defined as: i = N1*N2*(proc-1) + N1*(n2-1) + n1, use
% proc = ceil( i/N1/N2 );
% apu = i - (proc-1)*N1*N2;
% n2 = ceil( apu/N1 );
% n1 = apu - (n2-1)*N1;
% 
%   n1n2temp=mod(i,N1*N2); 
%   if mod(n1n2temp,N1)==0,
%       n1=N1;
%   elseif mod(n1n2temp,N1)==1,
%       n1=1;
%   else
%       n1=mod(n1n2temp,N1);
%   end
%   if ceil(n1n2temp/N1)==0,
%       n2=N2;
%   else
%       n2=ceil(n1n2temp/N1);
%   end
%   proc = ceil(i/N1/N2);
  
%    n1n2temp=mod(i,N1*N2); 
%   if mod(n1n2temp,N1)==0,
%       n1=0;
%   else   
%       n1=N1-mod(n1n2temp,N1);
%   end
%   if ceil(n1n2temp/N1)==0,
%       n2=N2;
%   else
%       n2=ceil(n1n2temp/N1);
%   end
%   proc = ceil(i/N1/N2); 
  
return

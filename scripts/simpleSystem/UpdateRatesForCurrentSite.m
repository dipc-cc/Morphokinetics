function [v,Rh,Ra,Rd,Ph,Pa,Pd,rlevel]=UpdateRatesForCurrentSite(rlevel,v,Rh,Ra,Rd,Ph,Pa,Pd,hrt,art,drt,n1,n2,N1,N2,So,St,g,k)
%
%
%

%Rh = Rh - sum( v(n1,n2,1:6) );
Rh = Rh - v(n1,n2,1)...
        - v(n1,n2,2)...
        - v(n1,n2,3)...
        - v(n1,n2,4)...
        - v(n1,n2,5)...
        - v(n1,n2,6);
Ra = Ra - v(n1,n2,7);
Rd = Rd - v(n1,n2,8);
Ph = Ph - sign(v(n1,n2,1))...
        - sign(v(n1,n2,2))...
        - sign(v(n1,n2,3))...
        - sign(v(n1,n2,4))...
        - sign(v(n1,n2,5))...
        - sign(v(n1,n2,6));
Pa = Pa - sign(v(n1,n2,7));
Pd = Pd - sign(v(n1,n2,8));
r_old_vec(1:8)=v(n1,n2,1:8); % by Zhang
St(n1,n2) = max(St(n1,n2),0);
if So(n1,n2) == 0
    v(n1,n2,1:6)=0;
    v(n1,n2,7)=art( St(n1,n2)+1 );
    v(n1,n2,8)=0;
elseif So(n1,n2) == 1
    n1e=n1+1; if (n1e > N1), n1e = 1; end
    n1w=n1-1; if (n1w < 1), n1w = N1; end
    n2n=n2+1; if (n2n > N2), n2n = 1; end
    n2s=n2-1; if (n2s < 1), n2s = N2; end
    
    if So(n1e,n2 ) == 0, v(n1,n2,1)=hrt( St(n1,n2)+1 , St(n1e,n2 ) ); else v(n1,n2,1)=0; end
    if So(n1 ,n2n) == 0, v(n1,n2,2)=hrt( St(n1,n2)+1 , St(n1 ,n2n) ); else v(n1,n2,2)=0; end
    if So(n1w,n2n) == 0, v(n1,n2,3)=hrt( St(n1,n2)+1 , St(n1w,n2n) ); else v(n1,n2,3)=0; end
    if So(n1w,n2 ) == 0, v(n1,n2,4)=hrt( St(n1,n2)+1 , St(n1w,n2 ) ); else v(n1,n2,4)=0; end
    if So(n1 ,n2s) == 0, v(n1,n2,5)=hrt( St(n1,n2)+1 , St(n1 ,n2s) ); else v(n1,n2,5)=0; end
    if So(n1e,n2s) == 0, v(n1,n2,6)=hrt( St(n1,n2)+1 , St(n1e,n2s) ); else v(n1,n2,6)=0; end

    v(n1,n2,7)=0;
    v(n1,n2,8)=drt( St(n1,n2)+1 );
end
r_new_vec(1:8)=v(n1,n2,1:8); % by Zhang
%R = R + sum( v(n1,n2,1:8) );
Rh = Rh + v(n1,n2,1)...
        + v(n1,n2,2)...
        + v(n1,n2,3)...
        + v(n1,n2,4)...
        + v(n1,n2,5)...
        + v(n1,n2,6);
Ra = Ra + v(n1,n2,7);
Rd = Rd + v(n1,n2,8);
Ph = Ph + sign(v(n1,n2,1))...
        + sign(v(n1,n2,2))...
        + sign(v(n1,n2,3))...
        + sign(v(n1,n2,4))...
        + sign(v(n1,n2,5))...
        + sign(v(n1,n2,6));
Pa = Pa + sign(v(n1,n2,7));
Pd = Pd + sign(v(n1,n2,8));

iL = 8*N2*(n1-1) + 8*(n2-1) + 1;
iR = iL+7;
cell_index_vec(1:8)=iL:iR;

% rlevel(k).element(cell_index_vec(1:8)) = r_new_vec(1:8);
% i = cell_index_vec(1:8);
% for lev = k-1:-1:1
%     i = ceil( i/g );
%     rlevel(lev).element(i) = rlevel(lev).element(i) - r_old_vec(1:8) + r_new_vec(1:8);
% end

%return

for j=1:8
  rlevel(k).element(cell_index_vec(j)) = r_new_vec(j);
  i = cell_index_vec(j);
  for lev = k-1:-1:1
    i = ceil( i/g );
    rlevel(lev).element(i) = rlevel(lev).element(i) - r_old_vec(j) + r_new_vec(j);
  end
end

return

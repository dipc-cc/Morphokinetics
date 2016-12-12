function PlotSOnST(So,St,l,numST,N1,N2)
%
% Plot currently occupied sites. Use a different color for the following
% site types:
%
% st = 0 ----> black  (k)
% st = 1 ----> green  (g)
% st = 2 ----> red    (r)
% st = 3 ----> blue   (b)
% st >= 4 ---> cyan   (c)
%
i0=0;
x0=zeros(numST(1),1);
y0=x0;

i1=0;
x1=zeros(numST(2),1);
y1=x1;

i2=0;
x2=zeros(numST(3),1);
y2=x2;

i3=0;
x3=zeros(numST(4),1);
y3=x3;

i4p=0;
x4p=zeros(numST(5),1);
y4p=x4p;

for n1=1:N1
   for n2=1:N2
       if ( So(n1,n2) == 1 )
           if ( St(n1,n2) == 0 )
               i0=i0+1;
               x0(i0)=l*( n1+0.5*n2 );
               y0(i0)=l*sqrt(3)*0.5*n2;
           elseif ( St(n1,n2) == 1 )
               i1=i1+1;
               x1(i1)=l*( n1+0.5*n2 );
               y1(i1)=l*sqrt(3)*0.5*n2;
           elseif ( St(n1,n2) == 2 )
               i2=i2+1;
               x2(i2)=l*( n1+0.5*n2 );
               y2(i2)=l*sqrt(3)*0.5*n2;
           elseif ( St(n1,n2) == 3 )
               i3=i3+1;
               x3(i3)=l*( n1+0.5*n2 );
               y3(i3)=l*sqrt(3)*0.5*n2;
           elseif ( St(n1,n2) > 3 )
               i4p=i4p+1;
               x4p(i4p)=l*( n1+0.5*n2 );
               y4p(i4p)=l*sqrt(3)*0.5*n2;
           end
       end
   end
end

clf

%Plot the boundaries
plot(l*[0 n1],l*[0 0],'r-')
hold on
plot(l*[n1 n1+0.5*n2],l*[0 sqrt(3)*0.5*n2],'r-')
plot(l*[n1+0.5*n2 0.5*n2],l*[sqrt(3)*0.5*n2 sqrt(3)*0.5*n2],'r-')
plot(l*[0.5*n2 0],l*[sqrt(3)*0.5*n2 0],'r-')

%Plot the occupied sites:
plot(x0,y0,'k.')
plot(x1,y1,'g.')
plot(x2,y2,'r.')
plot(x3,y3,'b.')
plot(x4p,y4p,'c.')

axis equal

return


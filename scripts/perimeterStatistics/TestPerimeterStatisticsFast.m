function [Hist_angles,Hist_events,Hist_hops]=TestPerimeterStatisticsFast(NE,NRW,IntRadius)

  DeterminePerimeterStatistics=1;
  %fprintf(1,'Executing TestPerimeterStatisticsFast with radius %d\n', IntRadius);
  NN=NE;
  ees=sign( rand(1,NN) - 0.5 ); %Move along positive/negative direction
  eett=rand(1,NN);
  ee1=max(2, ceil( eett*3 ))-2; %Move along direction 1
  ee2=-(min(2, ceil( eett*3 ))-2); %Move along direction 2
  ee3=1-ee1-ee2; %Move along direction 3

  c=0.5;
  s=sqrt(3)/2;

  Radius=IntRadius;
  Radius2=Radius*Radius;

  i=0;
  k=0;
  Hist_angles=zeros(1,360);
  Hist_events=zeros(1,NE);
  Hist_hops=zeros(1,360);
  randomNumbers=rand(1,NRW);
  for rw=1:NRW %Index for each random walk
    x=floor(IntRadius*0.95*cos(randomNumbers(rw)*2*pi));
    y=floor(IntRadius*0.95*sin(randomNumbers(rw)*2*pi));
    Outside=0;
    
    for j=1:NE
      i=i+1;
      k=k+1;
      dx =      (    1*ee1(k)    +c*ee2(k)      -c*ee3(k))*ees(k);
      dy =      (    0*ee1(k)    +s*ee2(k)      +s*ee3(k))*ees(k);
      x = x + dx;
      y = y + dy;
      apu2 = x*x + y*y;

      if k==NN
	ees=sign( rand(1,NN) - 0.5 ); %Move along positive/negative direction
	eett=rand(1,NN);
	ee1=max(2, ceil( eett*3 ))-2; %Move along direction 1
	ee2=-(min(2, ceil( eett*3 ))-2); %Move along direction 2
	ee3=1-ee1-ee2; %Move along direction 3
	k = 0;
      end    

      % it goes out
      if Outside == 0 && apu2 >= Radius2 
	OutboundAngle=acosd(x/sqrt(apu2));
	OutboundEvent=j;
	Outside=1;
      % it comes back
      elseif Outside == 1 && apu2 <= Radius2
	InboundAngle=acosd(x/sqrt(apu2));
	InboundEvent=j;
	Outside=0;
	IntAngle=round(InboundAngle-OutboundAngle)+180;
	if IntAngle == 0, IntAngle=360; end
	IntEvent=InboundEvent-OutboundEvent;
	Hist_angles(IntAngle)=Hist_angles(IntAngle)+1;
	Hist_events(IntEvent)=Hist_events(IntEvent)+1;
	Hist_hops(IntAngle)=Hist_hops(IntAngle)+IntEvent;
	if DeterminePerimeterStatistics==1
          break
	end
      end    
    end
    
  end

  return
end


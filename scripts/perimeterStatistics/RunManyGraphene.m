function [Hist_angles,Hist_events,Hist_hops]=RunManyGraphene(IntRadius,filename)

  events=1e7;  %Number of events
  randomWalks=5e3; %Number of random walks
  totalRandomWalks=1e7; %The "size" of iteration
  Hist_angles=zeros(1,360);
  Hist_events=zeros(1,events);
  Hist_hops=zeros(1,360);
  tmpFilename=['tmp' filename];
  iteration=0;
  for iii=0:randomWalks:totalRandomWalks
    iteration=iteration+1;
    t=cputime;
    [DHist_angles,DHist_events,DHist_hops]=PerimeterStatisticsGraphene(events,randomWalks,IntRadius);    
    Hist_angles=Hist_angles+DHist_angles;
    Hist_events=Hist_events+DHist_events;
    Hist_hops=Hist_hops+DHist_hops;

    %% xx=[-179:1:180];
    
    %% figure(4), clf, semilogy(xx,Hist_angles/sum(Hist_angles),'bo'), axis auto
    %% xlabel('Re-entry angle')
    %% ylabel('Probability = Counts / Sum(Counts)')
    %% title('Distribution of re-entry angles')
    %% set(gca,'XLim',[-180 180])

    %% figure(5), clf, loglog(Hist_events/sum(Hist_events),'kd'), axis auto
    %% xlabel('Number of events until re-entry')
    %% ylabel('Probability = Counts / Sum(Counts)')
    %% title('Number of events until re-entry')

    %% figure(6), clf, semilogy(xx,Hist_hops./(Hist_angles+1e-16),'rs'), axis auto
    %% xlabel('Re-entry angle')
    %% ylabel('Counts')
    %% title('Average number of hops until re-entry at given angle')
    %% set(gca,'XLim',[-180 180])

    %% pause(0.25)

    fid=1;
    fprintf(fid,'Number of evaluated re-entries: %10i   |  CPUtime: %8.4e s \n',iteration*randomWalks,cputime-t);
    fprintf(fid,'Iteration %d of %d\n', iteration, totalRandomWalks/randomWalks);
    
    if mod(iii,5e5) == 0
      fprintf(1,'saving %d\n',iii);
      save(tmpFilename,'Hist_angles','Hist_events','Hist_hops');
    end
  end

  return

end

function Loop

  fprintf(1,'Looping\n')
  for IntRadius=[320 160 80 40 20 10 ]
    filename=['gHist_' num2str(IntRadius,'%3.3i')]
    [Hist_angles,Hist_events,Hist_hops]=RunManyGraphene(IntRadius,filename);
    save(filename,'Hist_angles','Hist_events','Hist_hops')
  end

  return
end

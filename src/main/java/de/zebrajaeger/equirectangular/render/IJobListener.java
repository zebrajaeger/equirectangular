package de.zebrajaeger.equirectangular.render;

/**
 * Created by lars on 11.07.2015.
 */
public interface IJobListener {

  void onProcessImage(int lines, int pos);
}

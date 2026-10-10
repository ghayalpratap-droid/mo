let context;
export function playClick() {
  try {
    const Audio = window.AudioContext || window.webkitAudioContext;
    if (!Audio) return;
    context ||= new Audio();
    if (context.state === "suspended") context.resume().catch(() => {});
    const osc = context.createOscillator(),
      gain = context.createGain();
    osc.connect(gain);
    gain.connect(context.destination);
    osc.type = "sine";
    osc.frequency.setValueAtTime(700, context.currentTime);
    osc.frequency.exponentialRampToValueAtTime(350, context.currentTime + 0.06);
    gain.gain.setValueAtTime(0.035, context.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.001, context.currentTime + 0.075);
    osc.start();
    osc.stop(context.currentTime + 0.08);
  } catch {
    /* Sound is optional if the browser blocks audio. */
  }
}

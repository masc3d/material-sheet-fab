namespace LeoBridge.TestWindow
{
    partial class FormMain
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.uxMessage = new System.Windows.Forms.Label();
            this.SuspendLayout();
            // 
            // uxMessage
            // 
            this.uxMessage.AutoSize = true;
            this.uxMessage.Location = new System.Drawing.Point(13, 13);
            this.uxMessage.Name = "uxMessage";
            this.uxMessage.Size = new System.Drawing.Size(16, 13);
            this.uxMessage.TabIndex = 0;
            this.uxMessage.Text = "---";
            // 
            // FormMain
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(360, 336);
            this.Controls.Add(this.uxMessage);
            this.Name = "FormMain";
            this.Text = "FormMain";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        public System.Windows.Forms.Label uxMessage;

    }
}